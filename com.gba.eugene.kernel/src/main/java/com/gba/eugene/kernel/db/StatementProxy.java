package com.gba.eugene.kernel.db;

import com.gba.eugene.kernel.exceptions.DBException;
import com.gba.eugene.kernel.util.CCachedRowSet;
import com.gba.eugene.kernel.util.CStatementVO;
import com.gba.eugene.kernel.util.DB;
import com.gba.eugene.kernel.util.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementProxy implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(StatementProxy.class);

    protected Connection m_conn = null;
    private boolean close = false;
    /** Used if local					*/
    protected transient Statement p_stmt = null;
    /**	Value Object					*/
    protected CStatementVO p_vo = null;

    /**
     * @param resultSetType
     * @param resultSetConcurrency
     * @param trxName
     */
    public StatementProxy(int resultSetType, int resultSetConcurrency, String trxName) {
        p_vo = new CStatementVO (resultSetType, resultSetConcurrency);
        p_vo.setTrxName(trxName);

        init();
    }

    /**
     * @param vo
     */
    public StatementProxy(CStatementVO vo) {
        p_vo = vo;
        init();
    }

    //for subclass
    protected StatementProxy() {}

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if (name.equals("executeQuery")|| name.equals("executeUpdate") || name.equals("execute") || name.equals("addBatch")){
            String sql = (String)args[0];
            // p_vo.setSql(DB.getDatabase().convertStatement(sql));
            p_vo.setSql(sql);
            args[0] = p_vo.getSql();
        }
        else if (name.equals("close") && (args == null || args.length == 0)) {
            close();
            return null;
        } else if (name.equals("getRowSet") && (args == null || args.length == 0)) {
            return getRowSet();
        } else if (name.equals("isClosed") && (args == null || args.length == 0)) {
            return close;
        } else if (name.equals("finalize") && (args == null || args.length == 0)) {
            if (p_stmt != null && !close)
            {
                this.close();
            }
            return null;
        } else if (name.equals("commit") && (args == null || args.length == 0)) {
            commit();
            return null;
        } else if (name.equals("getSql") && (args == null || args.length == 0)) {
            return getSql();
        } else if (name.equals("equals") && (args != null && args.length == 1)) {
            return equals(args[0]);
        }
        Method m = p_stmt.getClass().getMethod(name, method.getParameterTypes());
        try
        {
            return m.invoke(p_stmt, args);
        }
        catch (InvocationTargetException e)
        {
            throw DB.getSQLException(e);
        }
    }

    /**
     * Initialise the statement wrapper object
     */
    protected void init(){
        try{
            Connection conn = null;
            TransactionManager trx = p_vo.getTrxName() == null ? null : TransactionManager.get(p_vo.getTrxName(), false);
            if(trx != null)
            {
                conn = trx.getConnection();
            }
            else{
                m_conn = DB.createConnection(true, Connection.TRANSACTION_READ_COMMITTED);
                conn = m_conn;
            }
            if (conn == null)
                throw new DBException("No Connection");
            p_stmt = conn.createStatement(p_vo.getResultSetType(), p_vo.getResultSetConcurrency());
        }
        catch (SQLException e)
        {
            log.error( "CStatement", e);
            throw new DBException(e);
        }
    }

    private void commit() throws SQLException{
        if (m_conn != null && !m_conn.getAutoCommit())
        {
            m_conn.commit();
        }
    }

    /**
     * 	Get Sql
     *	@return sql
     */
    public String getSql()
    {
        if (p_vo != null)
            return p_vo.getSql();
        return null;
    }	//	getSql

    /**
     * Close
     * @throws SQLException
     * @see java.sql.Statement#close()
     */
    protected void close () throws SQLException {
        if (close) return;

        try{
            if (p_stmt != null){
                DB.close(p_stmt);
                p_stmt = null;
            }
        }
        finally {
            if (m_conn != null)
            {
                try
                {
                    m_conn.close();
                }
                catch (Exception e)
                {}
            }
            m_conn = null;
            close = true;
        }
    }

    /**
     * 	Execute the wrapped statement and return row set
     * 	@return RowSet
     * 	@throws SQLException
     *  @see java.sql.PreparedStatement#executeQuery()
     */
    protected RowSet getRowSet(){
        RowSet rowSet = null;
        ResultSet rs = null;
        try
        {
            rs = p_stmt.executeQuery(p_vo.getSql());
            rowSet = CCachedRowSet.getRowSet(rs);
        }
        catch (Exception ex){
            log.error(p_vo.toString(), ex);
            throw new RuntimeException(ex);
        }
        finally
        {
            DB.close(rs);
            rs = null;
            DB.close(rowSet);
            rowSet = null;
        }
        return rowSet;
    }
}
