package com.gba.eugene.kernel.util;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface CStatement extends Statement {

    /**
     * 	Get Sql
     *	@return sql
     */
    public String getSql();

    /**
     * 	Execute Query
     * 	@return ResultSet or RowSet
     * 	@throws SQLException
     * @see java.sql.PreparedStatement#executeQuery()
     */
    public RowSet getRowSet();

    /**
     * @return boolean
     * @throws SQLException
     */
    @Override
    public boolean isClosed() throws SQLException;

    /**
     *
     * @throws SQLException
     */
    public void commit() throws SQLException;

}
