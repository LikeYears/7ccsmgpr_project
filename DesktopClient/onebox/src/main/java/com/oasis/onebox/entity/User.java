package com.oasis.onebox.entity;

import com.oasis.onebox.dao.DownloadTaskDao;
import com.oasis.onebox.download.DownloadTask;
import com.oasis.onebox.listener.InitialListener;
import com.oasis.onebox.tool.EncodeTool;
import com.oasis.onebox.tool.JDBCTool;
import com.oasis.onebox.tool.StringTool;
import com.oasis.onebox.tool.UserAgent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class User {
    private static final Log logger = LogFactory.getLog(User.class);

    private static Map<String, User> userlist = new HashMap<String, User>();

    private String username;
    private String password;
    private String directory;

    private int md5times;
    private String uuid;// random id
    private String token;
    private long expirationtime;// expirationtime of user
    private String realPathHead = InitialListener.realPathHead;

    public User()
    {

    }

    private User(String username, String password, String directory) {
        this.username = username;
        this.password = password;
        this.directory = directory;
    }


    public void setExpirationtime(long expirationtime) {
        this.expirationtime = expirationtime;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getPassword() {
        return password;
    }
    public String getDirectory() {
        return directory;
    }

    public String getUsername() {
        return username;
    }

    public static boolean registerUser(String username, String password)
    {
        String directory = Paths.get(InitialListener.realPathHead,username).toString();

        String querysql = "select * from userlist where username='%s'";
        querysql = String.format(querysql, username);
        List<User> users = JDBCTool.executeQuery(querysql, User.class);
        if (users != null && !users.isEmpty()){
            logger.info("REGISTER SQL IS NULL");
            return  false;
        }
        else
        {
            String sql = "insert into userlist values ('%s','%s','%s')";
            String escapeSql = directory.replace("\\","\\\\");
            sql = String.format(sql, username, password, escapeSql);
            if (JDBCTool.executeUpdate(sql)>0)
            {
                logger.info("REGISTER SQL IS OK");
                // create download directory
                boolean flat1 = false;
                boolean flat2 = false;
                try
                {
                    createUserDir(directory);flat1 = true;
                    DownloadTask.createUserDownloadDir(username, directory); flat2 = true;
                    userlist.put(username, new User(username, password, directory));
                    return true;
                }
                catch (IOException e)
                {
                    if (!flat1) {
                        logger.error("UserDir::crate user dir fail, maybe low access level to dir");
                    }
                    if (!flat2) {
                        logger.error("DownloadDir::user dir not exit or low access level to dir");
                    }
                }
            }
        }
        return false;
    }


    public static User loginAuth(String username, String password)
    {
        String querysql = "select * from userlist where username='%s' and password='%s'";
        querysql = String.format(querysql, username,password);
        List<User> users = JDBCTool.executeQuery(querysql, User.class);
        if (users != null && !users.isEmpty()) {
            User u1 = users.get(0);
            userlist.put(u1.getUsername(),u1);
            logger.info("USER NAME::"+u1.getUsername());
            return u1;
        }
        else {
            logger.info("LOGIN FAIL::EMPTY OR NULL");
            return null;
        }

    }

    public synchronized String createToken(HttpServletRequest request)
    {
        if (userlist.containsKey(token)) {
            userlist.remove(token);
        }
        uuid = StringTool.getUUID();
        String token = new StringBuilder().append(username).append(UserAgent.getIP(request)).append(UserAgent.getUserAgentID(request))
                .append(uuid).toString();
        int x = (int) (Math.random() * 100);
        token = EncodeTool.MD5(token, x);
        md5times = x;
        this.expirationtime = new Date().getTime() + 30 * 60 * 1000;
        userlist.put(token, this);
        return token;
    }


    public static String getUserDirectory(String username) {
        if (userlist.containsKey(username)) {
            User u = userlist.get(username);
            return u.getDirectory();
        }
        return null;
    }


    public static User checkToken(String _token, HttpServletRequest request) {
        if (userlist.containsKey(_token)) {
            User u = userlist.get(_token);
            if (u.expirationtime < new Date().getTime()) {
                return null;
            }
            String token = new StringBuilder().append(u.username).append(UserAgent.getIP(request)).append(UserAgent.getUserAgentID(request))
                    .append(u.uuid).toString();
            if (EncodeTool.MD5(token, u.md5times).equals(_token)) {
                u.setExpirationtime(new Date().getTime() + 30 * 60 * 1000);
                return u;
            } else {
                return null;
            }
        }
        return null;
    }

    public static void createUserDir(String directory) throws IOException {
        Path userDir = Paths.get(directory);
        if (!Files.exists(userDir, new LinkOption[]{LinkOption.NOFOLLOW_LINKS }))
        {
            Files.createDirectory(userDir);
        }
    }

}
