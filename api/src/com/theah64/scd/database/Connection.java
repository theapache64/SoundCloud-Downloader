package com.theah64.scd.database;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by theapache64 on 10/13/2015.
 */
public class Connection {

    public static final boolean debugMode = true;
    private static DataSource ds;

    public static java.sql.Connection getConnection() {

        try {

            if (ds == null) {
                final Context initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:/comp/env");
                ds = (DataSource) envContext.lookup(debugMode ? "jdbc/scdLocal" : "jdbc/scdRemote");
            }

            return ds.getConnection();

        } catch (NamingException | SQLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Connection error : " + e.getMessage());
        }
    }

    /*//Local credentials
    private static final String LC_HOST = "localhost";
    private static final String LC_PORT = "3306";
    private static final String LC_USERNAME = "root";
    private static final String LC_PASSWORD = "mike";

    //Remote credentials
    private static final String RM_USERNAME = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
    private static final String RM_PASSWORD = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
    private static final String RM_HOST = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
    private static final String RM_PORT = System.getenv("OPENSHIFT_MYSQL_DB_PORT");

    private static final String DATABASE_NAME = "xrob";*/

   /* private static final String SQL_CONNECTION_URL = String.format(
            "jdbc:mysql://%s:%s/%s",
            debugMode ? LC_HOST : RM_HOST,
            debugMode ? LC_PORT : RM_PORT,
            DATABASE_NAME);*/

   /* public static java.sql.Connection getDriverManagerConnection() {


        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            return DriverManager.getConnection(SQL_CONNECTION_URL,
                    debugMode ? LC_USERNAME : RM_USERNAME,
                    debugMode ? LC_PASSWORD : RM_PASSWORD
            );
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Error("Failed to get database connection : " + e.getMessage());
        }
    }*/


    public static boolean isDebugMode() {
        return debugMode;
    }
}
