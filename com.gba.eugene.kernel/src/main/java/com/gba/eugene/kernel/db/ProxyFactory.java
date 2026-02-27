package com.gba.eugene.kernel.db;

import com.gba.eugene.kernel.util.CCallableStatement;
import com.gba.eugene.kernel.util.CPreparedStatement;
import com.gba.eugene.kernel.util.CStatement;
import com.gba.eugene.kernel.util.CStatementVO;

import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * Factory class to instantiate dynamic proxy for CStatement, CPreparedStatement and CCallableStatement
 * @author Low Heng Sin
 *
 */
public class ProxyFactory {

    /**
     * @param resultSetType
     * @param resultSetConcurrency
     * @param trxName
     * @return new CStatement proxy instance
     */
    public static CStatement newCStatement(int resultSetType, int resultSetConcurrency, String trxName){
        return (CStatement) Proxy.newProxyInstance(CStatement.class.getClassLoader(), new Class[]{CStatement.class},
                new StatementProxy(resultSetType, resultSetConcurrency, trxName));
    }

    /**
     * @param resultSetType
     * @param resultSetConcurrency
     * @param sql
     * @param trxName
     * @return new CPreparedStatement proxy instance
     */
    public static CPreparedStatement newCPreparedStatement(int resultSetType,
                                                           int resultSetConcurrency, String sql, String trxName) {
        return (CPreparedStatement) Proxy.newProxyInstance(CPreparedStatement.class.getClassLoader(),
                new Class[]{CPreparedStatement.class},
                new PreparedStatementProxy(resultSetType, resultSetConcurrency, sql, trxName));
    }

    /**
     * @param resultSetType
     * @param resultSetConcurrency
     * @param sql
     * @param connection
     * @return new CPreparedStatement proxy instance
     */
    public static CPreparedStatement newCPreparedStatement(int resultSetType,
                                                           int resultSetConcurrency, String sql, Connection connection){
        return (CPreparedStatement) Proxy.newProxyInstance(CPreparedStatement.class.getClassLoader(),
                new Class[]{CPreparedStatement.class},
                new PreparedStatementProxy(resultSetType, resultSetConcurrency, sql, connection));
    }

    /**
     * @param resultSetType
     * @param resultSetConcurrency
     * @param sql
     * @param trxName
     * @return new CCallableStatement proxy instance
     */
    public static CCallableStatement newCCallableStatement(int resultSetType,
                                                           int resultSetConcurrency, String sql, String trxName) {
        return (CCallableStatement) Proxy.newProxyInstance(CCallableStatement.class.getClassLoader(),
                new Class[]{CCallableStatement.class},
                new CallableStatementProxy(resultSetType, resultSetConcurrency, sql, trxName));
    }

    /**
     * @param info
     * @return new CStatement proxy instance
     */
    public static CStatement newCStatement(CStatementVO info) {
        return (CStatement) Proxy.newProxyInstance(CStatement.class.getClassLoader(),
                new Class[]{CPreparedStatement.class},
                new StatementProxy(info));
    }

    /**
     * @param info
     * @return new CPreparedStatement proxy instance
     */
    public static CPreparedStatement newCPreparedStatement(CStatementVO info) {
        return (CPreparedStatement) Proxy.newProxyInstance(CPreparedStatement.class.getClassLoader(),
                new Class[]{CPreparedStatement.class},
                new PreparedStatementProxy(info));
    }

    /**
     * @param info
     * @return new CCallableStatement proxy instance
     */
    public static CCallableStatement newCCallableStatement(CStatementVO info) {
        return (CCallableStatement) Proxy.newProxyInstance(CCallableStatement.class.getClassLoader(),
                new Class[]{CCallableStatement.class},
                new CallableStatementProxy(info));
    }




}
