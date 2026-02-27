package com.gba.eugene.kernel.util;

import com.gba.eugene.kernel.db.Database;
import com.gba.eugene.kernel.db.DatabaseConnection;
import com.gba.eugene.kernel.db.SystemDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DB {
    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(DB.class);
    /** Connection Descriptor */
    private static DatabaseConnection s_cc = null;
    /** Lock Object */
    private static Object s_ccLock = new Object();

    public synchronized static void setDBTarget(DatabaseConnection cc){
        if(cc == null)
            throw new IllegalArgumentException("Connection is NULL");

        if(s_cc != null && s_cc.equals(cc))
            return;

        DB.closeTarget();

        synchronized (s_ccLock){
            s_cc = cc;
        }

        s_cc.setDataSource();
    }

    public static Connection getConnection(){
        return getConnection(true);
    }

    public static Connection getConnection(boolean autoCommit){
        return createConnection(autoCommit, Connection.TRANSACTION_READ_COMMITTED);
    }

    /**
     *	Create new Connection.<br/>
     *  The connection must be closed explicitly by the caller.<br/>
     *  Usually, developer should not call this directly.
     *
     *  @param autoCommit auto commit
     *  @param trxLevel - Connection.TRANSACTION_READ_UNCOMMITTED, Connection.TRANSACTION_READ_COMMITTED, Connection.TRANSACTION_REPEATABLE_READ, or Connection.TRANSACTION_READ_COMMITTED.
     *  @return Connection connection
     */
    public static Connection createConnection(boolean autoCommit, int trxLevel){
        Connection conn = s_cc.getConnection(autoCommit, trxLevel);
        if(conn == null)
            throw new IllegalArgumentException("DB.createConnection - @NoDBConnection@");

        try {
            if (conn != null && conn.getAutoCommit() != autoCommit)
            {
                throw new IllegalStateException("Failed to set the requested auto commit mode on connection. [autoCommit=" + autoCommit +"]");
            }
        } catch (SQLException e) {}

        return conn;
    }

    public static boolean isConnected(){
        if (s_cc == null) return false;

        //get connection
        boolean success = false;
        try
        {
            Connection conn = getConnection();   //  try to get a connection
            if (conn != null)
            {
                conn.close();
                success = true;
            }
            else success = false;
        }
        catch (Exception e)
        {
            success = false;
        }
        return success;
    }

    /**
     *	Close DB connection profile
     */
    public static void closeTarget()
    {

        boolean closed = false;

        //  CConnection
        if (s_cc != null)
        {
            closed = true;
            s_cc.setDataSource(null);
        }
        s_cc = null;
        if (closed)
            log.info("closed");
    }	//	closeTarget

    public static SystemDatabase getDatabase(){
        if (s_cc != null)
            return s_cc.getDatabase();
        log.error("No Database Connection");
        return null;
    }

    public static SystemDatabase getDatabase(String url){
        return Database.getDatabaseFromURL(url);
    }

    public static String getDatabaseInfo(){
        if(s_cc != null)
            return s_cc.getDBInfo();

        return "No Database";
    }

    public static void close(Statement st){
        try{
            if (st != null)
                st.close();
        }
        catch (SQLException ex){
            ;
        }
    }

    /**
     * Convenient method to close result set
     * @param rs
     */
    public static void close( ResultSet rs) {
        try {
            if (rs!=null) rs.close();
        } catch (SQLException e) {
            ;
        }
    }

    /**
     * Try to get the SQLException from Exception
     * @param e Exception
     * @return SQLException if found or provided exception elsewhere
     */
    public static Exception getSQLException(Exception e)
    {
        Throwable e1 = e;
        while (e1 != null)
        {
            if (e1 instanceof SQLException)
                return (SQLException)e1;
            e1 = e1.getCause();
        }
        return e;
    }
}
