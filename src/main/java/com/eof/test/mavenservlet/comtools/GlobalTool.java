package com.eof.test.mavenservlet.comtools;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 默认配置加载
 */
public class GlobalTool {
    private static Logger logger = Logger.getLogger(GlobalTool.class);
    private static Map<String, String> mConf = null;

    private static int loadPropertyFile(String strFile) {
        mConf = new HashMap<String, String>();
        Properties property = new Properties();
        try {
            property.load(new FileInputStream(strFile));
        } catch (FileNotFoundException e) {
            logger.error("exception:" + e);
            return -1;
        } catch (IOException e) {
            logger.error("exception:" + e);
            return -2;
        }
        for (String key : property.stringPropertyNames()) {
            mConf.put(key, property.getProperty(key));
        }
        return 0;
    }

    public static String getKey(String key) {
        Object o = mConf.get(key);
        if (o != null) {
            return o.toString();
        } else {
            return "";
        }
    }

    public static String getKey(String key, String defaultValue) {
        if (mConf == null) {
            return defaultValue;
        }
        Object o = mConf.get(key);
        if (o != null) {
            return o.toString();
        } else {
            return defaultValue;
        }
    }

    public static int loadFile(String strFileName) {
        int iRet = loadPropertyFile(strFileName);
        if (iRet != 0) {
            return iRet;
        }
        return 0;
    }

    public static int loadDBConfig(String strFilename) {
        int iRet = loadPropertyFile(strFilename);
        if (iRet != 0) {
            return iRet;
        }

        return 0;
    }

    /**
     * 初始化数据库连接
     * @return
     */
    public static Connection initConn() {
        Connection conn = null;
        String strUrl = getDBUrl();
        logger.info("dbUrl:" + strUrl);
        try {
            conn = DriverManager.getConnection(strUrl, getKey("dbuser", "root"), getKey("dbpasswd", ""));
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("initConn getConnection:" + strUrl, e);
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     * @param conn
     * @param stPs
     */
    public static void closeConn(Connection conn, Statement stPs) {
        try {
            if (stPs != null) {
                stPs.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("closeConn:", e);
        }
    }

    private static String getDBUrl() {
        String dbname = getKey("dbname", "eofs_db");
        String dbIP = getKey("dbip", "127.0.0.1");
        String dbPort = getKey("dbport", "3306");
        String urlparam = "";
        StringBuffer sb = new StringBuffer("jdbc:mysql://");
        sb.append(dbIP).append(":").append(dbPort).append("/").append(dbname).append("?").append(urlparam);
        return sb.toString();
    }

    public static String getLocalIP() {
        String localIP = "";
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
        localIP = addr.getHostAddress();// 获得本机IP
        return localIP;
    }

    public static String getBasePath() {
        return getKey("projecthome", "D:/workplace/table_authority/table_authority");
    }

    public static String getCurrentHour() {
        Date date = new Date();
        SimpleDateFormat currentTime = new SimpleDateFormat("yyyyMMdd_HH");
        String strCurrent = currentTime.format(date);
        return strCurrent;
    }

    public static int init() {
        String propertiesfile = "global_cfg.conf";
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("com.mysql.jdbc.Driver ClassNotFoundException" + e.toString());
        }
        int iRet = GlobalTool.loadDBConfig(propertiesfile);
        if (iRet < 0) {
            System.out.println("init error");
            return iRet;
        }
        PropertyConfigurator.configure("log4j.properties");
        logger.info("init done");
        return 0;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

    }
}