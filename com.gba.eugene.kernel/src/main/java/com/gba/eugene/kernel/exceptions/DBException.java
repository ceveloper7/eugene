package com.gba.eugene.kernel.exceptions;

import java.sql.SQLException;

public class DBException extends SystemException{

    private String m_sql = null;

    public DBException(Exception e){
        super(e);
    }

    /**
     * Create a new DBException based on a SQLException and SQL Query
     * @param e exception
     * @param sql sql query
     */
    public DBException(SQLException e, String sql)
    {
        this(e);
        m_sql = sql;
    }

    /**
     * Create a new DBException
     * @param msg Message
     */
    public DBException(String msg)
    {
        super(msg);
    }
}
