package com.gba.eugene.kernel.db;

import com.gba.eugene.kernel.exceptions.DBException;
import com.gba.eugene.kernel.util.CStatementVO;
import com.gba.eugene.kernel.util.DB;
import com.gba.eugene.kernel.util.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class CallableStatementProxy extends PreparedStatementProxy{

    private static final Logger log = LoggerFactory.getLogger(CallableStatementProxy.class);

    public CallableStatementProxy(CStatementVO vo) {
        super(vo);
    }

    /**
     * @param resultSetType
     * @param resultSetConcurrency
     * @param sql0
     * @param trxName
     */
    public CallableStatementProxy(int resultSetType, int resultSetConcurrency,
                                  String sql0, String trxName) {
        super(resultSetType, resultSetConcurrency, sql0, trxName);
    }

    protected  void init(){
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
}
