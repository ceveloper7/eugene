package com.gba.eugene.kernel.util;

import com.gba.eugene.kernel.db.Database;
import com.gba.eugene.kernel.db.DatabaseConnection;
import com.gba.eugene.kernel.db.ProxyFactory;
import com.gba.eugene.kernel.db.SystemDatabase;
import com.gba.eugene.kernel.exceptions.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public final class DB {
    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(DB.class);
    /** Connection Descriptor */
    private static DatabaseConnection s_cc = null;
    /** Lock Object */
    private static Object s_ccLock = new Object();

    /** SQL Statement Separator "; "	*/
    public static final String SQLSTATEMENT_SEPARATOR = "; ";

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

    /**
     * 	Is connected to PostgreSQL DB ?
     *	@return true if connected to PostgreSQL
     */
    public static boolean isPostgreSQL()
    {
        if (s_cc != null)
            return s_cc.isPostgreSQL();
        log.error("No Database");
        return false;
    }	//	isPostgreSQL

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

    /**
     *	Create callable statement proxy
     *  @param sql
     *  @return Callable Statement
     */
    public static CallableStatement prepareCall(String sql)
    {
        return prepareCall(sql, ResultSet.CONCUR_UPDATABLE, null);
    }

    /**
     *	Create callable statement proxy
     *  @param SQL
     *  @param resultSetConcurrency
     *  @param trxName
     *  @return Callable Statement
     */
    public static CallableStatement prepareCall(String SQL, int resultSetConcurrency, String trxName)
    {
        if (SQL == null || SQL.length() == 0)
            throw new IllegalArgumentException("Required parameter missing - " + SQL);
        return ProxyFactory.newCCallableStatement(ResultSet.TYPE_FORWARD_ONLY, resultSetConcurrency, SQL,
                trxName);
    }	//	prepareCall

    /**
     *	Create prepare Statement proxy
     *  @param connection
     *  @param sql
     *  @return Prepared Statement
     */
    public static CPreparedStatement prepareStatement (Connection connection, String sql)
    {
        return prepareStatement(connection, sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }	//	prepareStatement

    /**
     *	Create prepare Statement proxy
     *  @param connection
     *  @param sql sql statement
     *  @param resultSetType - ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_SENSITIVE
     *  @param resultSetConcurrency - ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE
     *  @return Prepared Statement r/o or r/w depending on concur
     */
    public static CPreparedStatement prepareStatement(Connection connection, String sql,
                                                      int resultSetType, int resultSetConcurrency)
    {
        if (sql == null || sql.length() == 0)
            throw new IllegalArgumentException("No SQL");
        //
        return ProxyFactory.newCPreparedStatement(resultSetType, resultSetConcurrency, sql, connection);
    }	//	prepareStatement

    /**
     *	Create prepare Statement proxy
     *  @param sql
     * 	@param trxName transaction
     *  @return Prepared Statement
     */
    public static CPreparedStatement prepareStatement (String sql, String trxName)
    {
        return prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, trxName);
    }	//	prepareStatement

    /**
     *	Create prepare Statement proxy
     *  @param sql
     *  @param resultSetType - ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_SENSITIVE
     *  @param resultSetConcurrency - ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE
     * 	@param trxName transaction name
     *  @return Prepared Statement
     */
    public static CPreparedStatement prepareStatement(String sql,
                                                      int resultSetType, int resultSetConcurrency, String trxName)
    {
        if (sql == null || sql.length() == 0)
            throw new IllegalArgumentException("No SQL");
        //
        return ProxyFactory.newCPreparedStatement(resultSetType, resultSetConcurrency, sql, trxName);
    }	//	prepareStatement

    /**
     *	Create Statement proxy
     *  @return Statement
     */
    public static Statement createStatement()
    {
        return createStatement (ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, null);
    }	//	createStatement

    /**
     *	Create Statement Proxy.
     *  @param resultSetType - ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_SENSITIVE
     *  @param resultSetConcurrency - ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE
     * 	@param trxName transaction name
     *  @return Statement
     */
    public static Statement createStatement(int resultSetType, int resultSetConcurrency, String trxName)
    {
        return ProxyFactory.newCStatement(resultSetType, resultSetConcurrency, trxName);
    }	//	createStatement


    /**
     * Set parameters for given statement
     * @param stmt statements
     * @param params parameters array; if null or empty array, no parameters are set
     */
    public static void setParameters(PreparedStatement stmt, Object[] params)
            throws SQLException
    {
        if (params == null || params.length == 0) {
            return;
        }
        //
        for (int i = 0; i < params.length; i++)
        {
            setParameter(stmt, i+1, params[i]);
        }
    }

    /**
     * Set parameters for given statement
     * @param stmt statements
     * @param params parameters list; if null or empty list, no parameters are set
     */
    public static void setParameters(PreparedStatement stmt, List<?> params)
            throws SQLException
    {
        if (params == null || params.isEmpty())
        {
            return;
        }
        for (int i = 0; i < params.size(); i++)
        {
            setParameter(stmt, i+1, params.get(i));
        }
    }

    /**
     * Set PreparedStatement's parameter.<br/>
     * Similar with calling <code>pstmt.setObject(index, param)</code>
     * @param pstmt
     * @param index
     * @param param
     * @throws SQLException
     */
    public static void setParameter(PreparedStatement pstmt, int index, Object param)
            throws SQLException
    {
        if (param == null)
            pstmt.setObject(index, null);
        else if (param instanceof String)
            pstmt.setString(index, (String)param);
        else if (param instanceof Integer)
            pstmt.setInt(index, ((Integer)param).intValue());
        else if (param instanceof BigDecimal)
            pstmt.setBigDecimal(index, (BigDecimal)param);
        else if (param instanceof Timestamp)
            pstmt.setTimestamp(index, (Timestamp)param);
        else if (param instanceof Boolean)
            pstmt.setString(index, ((Boolean)param).booleanValue() ? "Y" : "N");
        else if (param instanceof byte[])
            pstmt.setBytes(index, (byte[]) param);
        else if (param instanceof Clob)
            pstmt.setClob(index, (Clob) param);
        else if (param.getClass().getName().equals("oracle.sql.BLOB"))
            pstmt.setObject(index, param);
        else
            throw new DBException("Unknown parameter type "+index+" - "+param);
    }

    /**
     *	Execute Update.
     *  saves "DBExecuteError" in Log
     *  @param sql sql
     * 	@param ignoreError if true, no execution error is reported
     * 	@param trxName transaction
     *  @return number of rows updated or -1 if error
     */
    public static int executeUpdate (String sql, boolean ignoreError, String trxName)
    {
        return executeUpdate (sql, ignoreError, trxName, 0);
    }	//	executeUpdate

    /**
     *	Execute Update.
     *  saves "DBExecuteError" in Log
     *  @param sql sql
     * 	@param ignoreError if true, no execution error is reported
     * 	@param trxName transaction
     *  @param timeOut optional timeOut parameter
     *  @return number of rows updated or -1 if error
     */
    public static int executeUpdate (String sql, boolean ignoreError, String trxName, int timeOut)
    {
        return executeUpdate (sql, null, ignoreError, trxName, timeOut);
    }


    /**
     *	Execute Update.
     *  saves "DBExecuteError" in Log
     *  @param sql sql
     *  @param param int param
     * 	@param trxName transaction
     *  @return number of rows updated or -1 if error
     */
    public static int executeUpdate (String sql, int param, String trxName)
    {
        return executeUpdate (sql, param, trxName, 0);
    }	//	executeUpdate

    /**
     *	Execute Update.
     *  saves "DBExecuteError" in Log
     *  @param sql sql
     *  @param param int param
     * 	@param trxName transaction
     *  @param timeOut optional timeOut parameter
     *  @return number of rows updated or -1 if error
     */
    public static int executeUpdate (String sql, int param, String trxName, int timeOut)
    {
        return executeUpdate (sql, new Object[]{Integer.valueOf(param)}, false, trxName, timeOut);
    }	//	executeUpdate

    /**
     *	Execute Update.
     *  saves "DBExecuteError" in Log
     *  @param sql sql
     *  @param param int parameter
     * 	@param ignoreError if true, no execution error is reported
     * 	@param trxName transaction
     *  @return number of rows updated or -1 if error
     */
    public static int executeUpdate (String sql, int param, boolean ignoreError, String trxName)
    {
        return executeUpdate (sql, param, ignoreError, trxName, 0);
    }	//	executeUpdate

    /**
     *	Execute Update.
     *  saves "DBExecuteError" in Log
     *  @param sql sql
     *  @param param int parameter
     * 	@param ignoreError if true, no execution error is reported
     * 	@param trxName transaction
     *  @param timeOut optional timeOut parameter
     *  @return number of rows updated or -1 if error
     */
    public static int executeUpdate (String sql, int param, boolean ignoreError, String trxName, int timeOut)
    {
        return executeUpdate (sql, new Object[]{Integer.valueOf(param)}, ignoreError, trxName, timeOut);
    }	//	executeUpdate


    /**
     *	Execute Update.
     *  saves "DBExecuteError" in Log
     *  @param sql sql
     *  @param params array of parameters
     * 	@param ignoreError if true, no execution error is reported
     * 	@param trxName optional transaction name
     *  @return number of rows updated or -1 if error
     */
    public static int executeUpdate (String sql, Object[] params, boolean ignoreError, String trxName)
    {
        return executeUpdate(sql, params, ignoreError, trxName, 0);
    }

    /**
     *	Execute Update.<br/>
     *  Saves "DBExecuteError" in Log.<br/>
     *  Developer is recommended to call {@link #executeUpdateEx(String, Object[], String, int)} instead.
     *  @param sql
     *  @param params array of parameters
     * 	@param ignoreError if true, no execution error is reported
     * 	@param trxName optional transaction name
     *  @param timeOut optional timeOut parameter
     *  @return number of rows updated or -1 if error
     */
    public static int executeUpdate (String sql, Object[] params, boolean ignoreError, String trxName, int timeOut)
    {
        if (sql == null || sql.length() == 0)
            throw new IllegalArgumentException("Required parameter missing - " + sql);
        verifyTrx(trxName);
        //
        int no = -1;
        CPreparedStatement cs = ProxyFactory.newCPreparedStatement(ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_UPDATABLE, sql, trxName);	//	converted in call

        try
        {
            setParameters(cs, params);
            //set timeout
            if (timeOut > 0)
            {
                cs.setQueryTimeout(timeOut);
            }
            no = cs.executeUpdate();
        }
        catch (Exception e)
        {
            e = getSQLException(e);
            if (ignoreError)
                log.error(cs.getSql() + " [" + trxName + "] - " +  e.getMessage());
            else
            {
                log.error( cs.getSql() + " [" + trxName + "]", e);
                log.error ("DBExecuteError", e);
            }
        }
        finally
        {
            //  Always close cursor
            close(cs);
            cs = null;
        }
        return no;
    }

    /**
     * Execute update and throw DBException if there are errors.
     * @param sql
     * @param params statement parameters
     * @param trxName transaction
     * @return number of rows updated
     * @throws SQLException
     */
    public static int executeUpdateEx (String sql, Object[] params, String trxName) throws DBException
    {
        return executeUpdateEx(sql, params, trxName, 0);
    }

    /**
     * Execute update and throw DBException if there are errors.
     * @param sql
     * @param params statement parameters
     * @param trxName transaction
     * @param timeOut optional timeOut parameter
     * @return number of rows updated
     * @throws DBException
     */
    public static int executeUpdateEx (String sql, Object[] params, String trxName, int timeOut) throws DBException
    {
        if (sql == null || sql.length() == 0)
            throw new IllegalArgumentException("Required parameter missing - " + sql);
        //
        verifyTrx(trxName);
        int no = -1;
        CPreparedStatement cs = ProxyFactory.newCPreparedStatement(ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_UPDATABLE, sql, trxName);	//	converted in call

        try
        {
            setParameters(cs, params);
            if (timeOut > 0)
            {
                {
                    cs.setQueryTimeout(timeOut);
                }
            }
            no = cs.executeUpdate();
        }
        catch (Exception e)
        {
            throw new DBException(e);
        }
        finally
        {
            close(cs);
            cs = null;
        }
        return no;
    }

    /**
     *	Execute multiple Update statements.<br/>
     *  Saves (last) "DBExecuteError" in Log.
     *  @param sql multiple sql statements separated by "; " SQLSTATEMENT_SEPARATOR
     * 	@param ignoreError if true, no execution error is reported
     * 	@param trxName optional transaction name
     *  @return number of rows updated or -1 if error
     */
    public static int executeUpdateMultiple (String sql, boolean ignoreError, String trxName)
    {
        if (sql == null || sql.length() == 0)
            throw new IllegalArgumentException("Required parameter missing - " + sql);
        int index = sql.indexOf(SQLSTATEMENT_SEPARATOR);
        if (index == -1)
            return executeUpdate(sql, null, ignoreError, trxName);
        int no = 0;
        //
        String statements[] = sql.split(SQLSTATEMENT_SEPARATOR);
        for (int i = 0; i < statements.length; i++)
        {
            if (log.isDebugEnabled()) log.debug("{}", statements[i]);
            no += executeUpdate(statements[i], null, ignoreError, trxName);
        }

        return no;
    }	//	executeUpdareMultiple

    /**
     *	Commit transaction
     *  @param throwException if true, re-throws exception
     * 	@param trxName transaction name
     *  @return true if not needed (trxName is null) or success
     *  @throws SQLException
     */
    public static boolean commit (boolean throwException, String trxName) throws SQLException,IllegalStateException
    {
        // Not on transaction scope, Connection are thus auto commit
        if (trxName == null)
        {
            return true;
        }

        try
        {
            TransactionManager trx = TransactionManager.get(trxName, false);
            if (trx != null)
                return trx.commit(true);

            if (throwException)
            {
                throw new IllegalStateException("Could not load transation with identifier: " + trxName);
            }
            else
            {
                return false;
            }
        }
        catch (SQLException e)
        {
            log.error("[" + trxName + "]", e);
            if (throwException)
                throw e;
            return false;
        }
    }	//	commit

    /**
     *	Rollback transaction
     *  @param throwException if true, re-throws exception
     * 	@param trxName transaction name
     *  @return true if not needed (trxName is null) or success
     *  @throws SQLException
     */
    public static boolean rollback (boolean throwException, String trxName) throws SQLException
    {
        // Not on transaction scope, Connection are thus auto commit/rollback
        if (trxName == null)
        {
            return true;
        }

        try
        {
            TransactionManager trx = TransactionManager.get(trxName, false);
            if (trx != null)
                return trx.rollback(true);

            if (throwException)
            {
                throw new IllegalStateException("Could not load transation with identifier: " + trxName);
            }
            else
            {
                return false;
            }
        }
        catch (SQLException e)
        {
            log.error("[" + trxName + "]", e);
            if (throwException)
                throw e;
            return false;
        }
    }	//	commit

    /**
     * 	Get Row Set.<br/>
     * 	When a Rowset is closed, it also closes the underlying connection.
     *	@param sql
     *	@return row set or null
     */
    public static RowSet getRowSet (String sql)
    {
        CStatementVO info = new CStatementVO (RowSet.TYPE_SCROLL_INSENSITIVE, RowSet.CONCUR_READ_ONLY, sql);
        CPreparedStatement stmt = null;
        RowSet retValue = null;
        try {
            stmt = ProxyFactory.newCPreparedStatement(info);
            retValue = stmt.getRowSet();
        } finally {
            close(stmt);
        }
        return retValue;
    }	//	getRowSet

    /**
     * Reset connection's auto commit to true and read only to false before closing it.
     * @param conn
     */
    public static void closeAndResetReadonlyConnection(Connection conn) {
        try {
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            conn.setReadOnly(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convenient method to close result set and statement
     * @param rs result set
     * @param st statement
     * @see #close(ResultSet)
     * @see #close(Statement)
     */
    public static void close(ResultSet rs, Statement st) {
        close(rs);
        close(st);
    }

    /**
     * Get int value from sql
     * @param trxName optional transaction name
     * @param sql
     * @param params collection of parameters
     * @return first value or -1
     * @throws DBException if there is any SQLException
     */
    public static int getSQLValueEx (String trxName, String sql, List<Object> params)
    {
        return getSQLValueEx(trxName, sql, params.toArray(new Object[params.size()]));
    }


    /**
     * Get int Value from sql
     * @param trxName optional transaction name
     * @param sql
     * @param params array of parameters
     * @return first value or -1 if not found
     * @throws DBException if there is any SQLException
     */
    public static int getSQLValueEx (String trxName, String sql, Object... params) throws DBException
    {
        int retValue = -1;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        if (trxName == null)
            conn = DB.createConnection(true, Connection.TRANSACTION_READ_COMMITTED);
        try
        {
            if (conn != null)
            {
                conn.setAutoCommit(false);
                conn.setReadOnly(true);
            }

            if (conn != null)
                pstmt = prepareStatement(conn, sql);
            else
                pstmt = prepareStatement(sql, trxName);
            setParameters(pstmt, params);
            rs = pstmt.executeQuery();
            if (rs.next())
                retValue = rs.getInt(1);
            else
            if (log.isDebugEnabled()) log.debug("No Value " + sql);
        }
        catch (SQLException e)
        {
            if (conn != null)
            {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            throw new DBException(e, sql);
        }
        finally
        {
            close(rs, pstmt);
            rs = null; pstmt = null;
            if (conn != null)
            {
                closeAndResetReadonlyConnection(conn);
            }
        }
        return retValue;
    }

    private static void verifyTrx(String trxName) {
        if (trxName != null && TransactionManager.get(trxName, false) == null) {
            // Using a trx that was previously closed or never opened
            // this is equivalent to commit without trx (autocommit)
            log.error("Transaction closed or never opened ("+trxName+") => (maybe time out)"); // severe?
        }
    }

}
