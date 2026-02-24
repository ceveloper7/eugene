package com.gba.eugene.kernel.db;

import com.gba.eugene.kernel.model.SystemConstants;
import com.gba.eugene.kernel.util.Util;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;



public class Database_PostgresSQL implements SystemDatabase{

    private static final Logger log = LoggerFactory.getLogger(Database_PostgresSQL.class);

    private String m_userName = null;
    private String m_connectionURL = null;
    private String m_dbName = null;

    public Database_PostgresSQL(){}

    /** Driver                  */
    private org.postgresql.Driver   s_driver = null;

    /** Default Port            */
    public static final int         DATABASE_DEFAULT_PORT = 5432;

    private volatile HikariDataSource m_ds;

    @Override
    public String getName() {
        return Database.DATABASE_POSTGRESQL;
    }

    @Override
    public String getDescription() {
        try{
            if (s_driver == null)
                getDriver();
        }
        catch (Exception ex){}

        if (s_driver == null)
            return s_driver.toString();

        return "There is no Driver";
    }

    @Override
    public java.sql.Driver getDriver() throws SQLException {
        if (s_driver == null){
            s_driver = new org.postgresql.Driver();
            DriverManager.registerDriver(s_driver);
            DriverManager.setLoginTimeout(Database.CONNECTION_TIMEOUT);
        }
        return s_driver;
    }

    @Override
    public int getStandardPort() {
        return DATABASE_DEFAULT_PORT;
    }

    @Override
    public String getConnectionURL(String dbHost, int dbPort, String dbName, String userName) {
        StringBuilder sb = new StringBuilder("jdbc:postgresql://")
                .append(dbHost)
                .append(":").append(dbPort)
                .append("/").append(dbName);

        String urlParameters = SystemConstants.getPostgresqlURLParameters();
        if (!Util.isEmpty(urlParameters)) {
            sb.append("?").append(urlParameters);
        }

        return sb.toString();
    }

    @Override
    public String getConnectionURL(String connectionURL, String userName) {
        m_userName = userName;
        m_connectionURL = connectionURL;
        return m_connectionURL;
    }

    @Override
    public Connection getDriverConnection(String dbUrl, String dbUid, String dbPwd) throws SQLException {
        getDriver();
        return DriverManager.getConnection(dbUrl, dbUid, dbPwd);
    }

    @Override
    public String getCatalog() {
        if (m_dbName != null)
            return m_dbName;

        return null;
    }

    @Override
    public String getSchema() {
        return "gba";
    }

    @Override
    public boolean supportsBLOB() {
        return true;
    }
}
