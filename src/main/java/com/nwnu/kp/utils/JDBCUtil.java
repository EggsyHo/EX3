package com.nwnu.kp.utils;
import com.alibaba.druid.util.JdbcUtils;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class JDBCUtil {
    private static final String driverClassName;
    private static final String url;
    private static final String username;
    private static final String password;
    static {
        Properties properties=new Properties();
        try {
            InputStream in = JdbcUtils.class.getClassLoader().getResourceAsStream("application.yaml");
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        driverClassName=properties.getProperty("driverClassName");
        url=properties.getProperty("url");
        username=properties.getProperty("username");
        password=properties.getProperty("password");
    }

    public static void loadDriver(){
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        Connection conn=null;
        loadDriver();
        try {
            conn= DriverManager.getConnection(url,username,password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }

    public static void release(Statement statement, Connection conn) throws SQLException {
        statement.close();
        conn.close();
    }

    public static void release(ResultSet rs,Statement statement, Connection conn) {
        try {
            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("关闭数据库失败");
        }
    }

    public static void recordJournal(String operation,String command) throws SQLException {
        Connection connection = JDBCUtil.getConnection();
        Statement statement = connection.createStatement();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=format.format(new Date());
        String insert="INSERT INTO `journal` (`operation`, `command`, `time`) " +
                "VALUES ('"+operation+"','"+command+"','"+time+"')";
        statement.execute(insert);
        JDBCUtil.release(statement,connection);
    }
}
