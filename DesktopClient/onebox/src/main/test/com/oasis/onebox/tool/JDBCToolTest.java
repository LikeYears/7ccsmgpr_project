package com.oasis.onebox.tool;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

import static org.junit.Assert.*;

public class JDBCToolTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getConnection() {
     String url="jdbc:mysql:@//localhost:3306/onebox";
     String user="root";
     String password=" ";
     Connection conn;
     PreparedStatement ps;
     ResultSet rs;
     Statement st ;

    }

    @Test
    public void releaseDB() {
    }

    @Test
    public void executeUpdate() {
    }

    @Test
    public void executeQuery() {
    }

    @Test
    public void handleResultSetToMapList() {
    }

    @Test
    public void transfterMapListToBeanList() {
    }
}
