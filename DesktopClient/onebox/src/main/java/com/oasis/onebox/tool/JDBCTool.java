package com.oasis.onebox.tool;


import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class JDBCTool {
    public static Connection getConnection() throws Exception {
        Properties properties = new Properties();
        InputStream inStream = JDBCTool.class.getClassLoader()
                .getResourceAsStream("/config.properties");
        properties.load(inStream);

        // 1. 准备获取连接的 4 个字符串: user, password, url, jdbcDriver
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String url= properties.getProperty("url");
        String jdbcDriver= properties.getProperty("jdbcDriver");

        // 2. 加载驱动: Class.forName(driverClass)
        Class.forName(jdbcDriver);

        // 3.获取数据库连接
        Connection connection = DriverManager.getConnection(url, user,
                password);
        return connection;
    }

    public static void releaseDB(ResultSet resultSet, Statement statement,
                                 Connection connection) {

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static int executeUpdate(String sql, Object... args) {
        int j = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = JDBCTool.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            j = preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTool.releaseDB(null, preparedStatement, connection);
        }
        return j;
    }

//    //JDBC METHOD 1
//    public static <T> List<T> executeQuery(String sql, Class<T> clazz ) {
//
//        List<T> list = new ArrayList<>();
//        Field[] fields = clazz.getDeclaredFields();
//        Connection connection = null;
//        PreparedStatement preparedStatement = null;
//        ResultSet resultSet = null;
//        try {
//            connection = JDBCTool.getConnection();
//            preparedStatement = connection.prepareStatement(sql);
//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                T t = clazz.newInstance();
//                for (Field field : fields) {
//                    field.setAccessible(true);
//                    FieldAlias inject = field.getAnnotation(FieldAlias.class);
//                    if (inject != null) {
//                        String alias = inject.alias();
//                        if (!alias.equals("")) {
//                            field.set(t, resultSet.getObject(alias));
//                        } else {
//                            field.set(t, resultSet.getObject(field.getName()));
//                        }
//                    }
//                }
//                list.add(t);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            JDBCTool.releaseDB(resultSet, preparedStatement, connection);
//        }
//
//        return list;
//    }

    //JDBC METHOD 2
    public static <T> List<T> executeQuery(String sql, Class<T> clazz, Object... args) {

        List<T> list = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            //1. 得到结果集
            connection = JDBCTool.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }

            resultSet = preparedStatement.executeQuery();

            //2. 处理结果集, 得到 Map 的 List, 其中一个 Map 对象
            //就是一条记录. Map 的 key 为 reusltSet 中列的别名, Map 的 value
            //为列的值.
            List<Map<String, Object>> values =
                    handleResultSetToMapList(resultSet);

            //3. 把 Map 的 List 转为 clazz 对应的 List
            //其中 Map 的 key 即为 clazz 对应的对象的 propertyName,
            //而 Map 的 value 即为 clazz 对应的对象的 propertyValue
            list = transfterMapListToBeanList(clazz, values);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTool.releaseDB(resultSet, preparedStatement, connection);
        }

        return list;
    }
    public static List<Map<String, Object>> handleResultSetToMapList(
            ResultSet resultSet) throws SQLException {
        // 5. 准备一个 List<Map<String, Object>>:
        // 键: 存放列的别名, 值: 存放列的值. 其中一个 Map 对象对应着一条记录
        List<Map<String, Object>> values = new ArrayList<>();

        List<String> columnLabels = getColumnLabels(resultSet);
        Map<String, Object> map = null;

        // 7. 处理 ResultSet, 使用 while 循环
        while (resultSet.next()) {
            map = new HashMap<>();

            for (String columnLabel : columnLabels) {
                Object value = resultSet.getObject(columnLabel);
                map.put(columnLabel, value);
            }

            // 11. 把一条记录的一个 Map 对象放入 5 准备的 List 中
            values.add(map);
        }
        return values;
    }
    private static List<String> getColumnLabels(ResultSet rs) throws SQLException {
        List<String> labels = new ArrayList<>();

        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            labels.add(rsmd.getColumnLabel(i + 1));
        }

        return labels;
    }

    public static <T> List<T> transfterMapListToBeanList(Class<T> clazz,
                                                         List<Map<String, Object>> values) throws Exception {

        List<T> result = new ArrayList<>();

        T bean = null;

        if (values.size() > 0) {
            for (Map<String, Object> m : values) {
                //通过反射创建一个其他类的对象
                bean = clazz.newInstance();

                for (Map.Entry<String, Object> entry : m.entrySet()) {
                    String propertyName = entry.getKey();
                    Object value = entry.getValue();

                    Field f=bean.getClass().getDeclaredField(propertyName);
                    f.setAccessible(true);
                    f.set(bean, value);

                    //BeanUtils.setProperty(bean, propertyName, value);
                }
                // 13. 把 Object 对象放入到 list 中.
                result.add(bean);
            }
        }

        return result;
    }
}
