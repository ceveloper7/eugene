package com.gba.eugene.kernel.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionManager{

    private static final Logger log = LoggerFactory.getLogger(TransactionManager.class);

    private Connection m_connection = null;
    private	String 		m_trxName = null;
    private boolean		m_active = false;
    private long m_startTime;

    /**	Transaction Cache					*/
    //private static CCache<String,TransactionManager> 	s_cache = null;	//	create change listener

    /**	Transaction Cache */
    private static final Map<String,TransactionManager> s_cache = new ConcurrentHashMap<String, TransactionManager>();

    /**************************************************************************
     * 	Transaction Constructor
     * 	@param trxName unique name
     */
    private TransactionManager (String trxName)
    {
        this (trxName, null);
    }	//	Trx


    /**
     * 	Transaction Constructor
     * 	@param trxName unique name
     *  @param con optional connection ( ignore for remote transaction )
     * 	 */
    private TransactionManager (String trxName, Connection con)
    {
        //	log.info (trxName);
        setTrxName (trxName);
        setConnection (con);
    }	//	Trx

    /**
     * 	Get Transaction
     *	@param trxName trx name
     *	@param createNew if false, null is returned if not found
     *	@return Transaction or null
     */
    public static synchronized TransactionManager get (String trxName, boolean createNew){
        if (trxName == null || trxName.isEmpty())
            throw new IllegalArgumentException ("No Transaction Name");
/*
        if(s_cache == null){
            s_cache= new CCache<String, TransactionManager>("Trx", 10, -1);
            s_cache.addVetoableChangeListener(new TransactionManager("controller"));
        }

        TransactionManager retValue = (TransactionManager)s_cache.get(trxName);
        if (retValue == null && createNew)
        {
            retValue = new TransactionManager (trxName);
            s_cache.put(trxName, retValue);
        }
        return retValue;

 */
        TransactionManager retValue = (TransactionManager) s_cache.get(trxName);
        if (retValue == null && createNew)
        {
            retValue = new TransactionManager (trxName);
            s_cache.put(trxName, retValue);
        }
        return retValue;
    }



    /**
     * 	Set Trx Name
     *	@param trxName transaction name
     */
    private void setTrxName (String trxName)
    {
        if (trxName == null || trxName.isEmpty())
            throw new IllegalArgumentException ("No Transaction Name");
        m_trxName = trxName;
    }	//	setName

    /**
     * 	Set Connection
     *	@param conn connection
     */
    private void setConnection (Connection conn)
    {
        if (conn == null)
            return;
        m_connection = conn;
        log.info("Connection=" + conn);
        try
        {
            m_connection.setAutoCommit(false);
        }
        catch (SQLException e)
        {
            log.error("connection", e);
        }
    }	//	setConnection

    /**
     * 	Start Trx
     *	@return true if trx started
     */
    public boolean start()
    {
        if (m_active)
        {
            log.warn("Trx in progress " + m_trxName);
            return false;
        }
        m_active = true;
        m_startTime = System.currentTimeMillis();
        return true;
    }	//	startTrx

    /**
     * 	Is Transaction Active
     *	@return true if transaction is active
     */
    public boolean isActive()
    {
        return m_active;
    }	//	isActive

    /**
     * Get connection
     * @return connection
     */
    public Connection getConnection()
    {
        return getConnection(true);
    }

    /**
     * 	Get or Create New Connection
     *  @param createNew if true, create new connection if the trx does not have one created yet
     *	@return connection
     */
    public synchronized Connection getConnection(boolean createNew){
        if(m_connection == null){
            if(createNew){
                if(!s_cache.containsKey(m_trxName)){
                    new Exception("Illegal to getConnection for Trx that is not register.").printStackTrace();
                    return null;
                }
                setConnection(DB.createConnection(false, Connection.TRANSACTION_READ_COMMITTED));
            }else{
                return null;
            }
        }
        if (!isActive())
            start();

        return m_connection;
    }

    /**
     * Commit
     * @param throwException if true, re-throws exception
     * @return true if success
     **/
    public boolean commit(boolean throwException) throws SQLException
    {
        //local
        try
        {
            if (m_connection != null)
            {
                m_connection.commit();
                //log.log(isLocalTrx(m_trxName) ? Level.FINE : Level.INFO, "**** " + m_trxName);
                log.trace ("**** {} " + m_trxName);
                m_active = false;
                return true;
            }
        }
        catch (SQLException e)
        {
            log.trace( m_trxName, e);
            if (throwException)
            {
                m_active = false;
                throw e;
            }
        }
        m_active = false;
        return false;
    }	//	commit

    /**
     * Commit
     * @return true if success
     */
    public boolean commit()
    {
        try
        {
            return commit(false);
        }
        catch(SQLException e)
        {
            return false;
        }
    }

    /**
     * 	Rollback
     *  @param throwException if true, re-throws exception
     *	@return true if success, false if failed or transaction already rollback
     */
    public boolean rollback(boolean throwException) throws SQLException
    {
        //local
        try
        {
            if (m_connection != null)
            {
                m_connection.rollback();
                log.info ("**** " + m_trxName);
                m_active = false;
                return true;
            }
        }
        catch (SQLException e)
        {
            log.error( m_trxName, e);
            if (throwException)
            {
                m_active = false;
                throw e;
            }
        }
        m_active = false;
        return false;
    }	//	rollback

    /**
     * Rollback
     * @return true if success, false if failed or transaction already rollback
     */
    public boolean rollback()
    {
        try {
            return rollback(false);
        } catch (SQLException e) {
            return false;
        }
    }
}
