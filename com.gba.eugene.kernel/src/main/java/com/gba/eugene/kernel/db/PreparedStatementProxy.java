package com.gba.eugene.kernel.db;

import com.gba.eugene.kernel.exceptions.DBException;
import com.gba.eugene.kernel.util.CCachedRowSet;
import com.gba.eugene.kernel.util.CStatementVO;
import com.gba.eugene.kernel.util.DB;
import com.gba.eugene.kernel.util.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PreparedStatementProxy extends StatementProxy{

    private static final Logger log = LoggerFactory.getLogger(PreparedStatementProxy.class);

    /**
     * @param resultSetType
     * @param resultSetConcurrency
     * @param sql0
     * @param trxName
     */
    public PreparedStatementProxy(int resultSetType, int resultSetConcurrency,
                                  String sql0, String trxName) {
        if (sql0 == null || sql0.isEmpty())
            throw new IllegalArgumentException("sql required");

        p_vo = new CStatementVO(resultSetType, resultSetConcurrency, sql0);

        p_vo.setTrxName(trxName);

        init();
    } // PreparedStatementProxy

    /**
     * @param resultSetType
     * @param resultSetConcurrency
     * @param sql0
     * @param connection
     */
    public PreparedStatementProxy(int resultSetType, int resultSetConcurrency,
                                  String sql0, Connection connection) {
        if (sql0 == null || sql0.length() == 0)
            throw new IllegalArgumentException("sql required");

        p_vo = new CStatementVO(resultSetType, resultSetConcurrency, sql0);

        init(connection);
    } // PreparedStatementProxy

    /**
     * @param vo
     */
    public PreparedStatementProxy(CStatementVO vo)
    {
        super(vo);
    }	//	PreparedStatementProxy

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
            log.error( p_vo.getSql(), e);
            throw new DBException(e);
        }
    }

    /**
     * Initialize the prepared statement wrapper object
     */
    protected void init(Connection connection) {
        try{
            p_stmt = connection.prepareStatement(p_vo.getSql(), p_vo.getResultSetType(), p_vo.getResultSetConcurrency());
        }
        catch (Exception e){
            log.error(p_vo.getSql(), e);
            throw new DBException(e);
        }
    }

    @Override
    protected RowSet getRowSet(){
        RowSet rowSet = null;
        ResultSet rs = null;
        PreparedStatement pstmt = (PreparedStatement) p_stmt;
        try{
            rs = pstmt.executeQuery();
            rowSet = CCachedRowSet.getRowSet(rs);
        }
        catch (Exception ex){
            log.error(p_vo.toString(), ex);
            throw new RuntimeException(ex);
        }
        finally {
            DB.close(rs);
        }
        return rowSet;
    }
}
