package com.gba.eugene.kernel.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class CacheMgt {

    private static final Logger log = LoggerFactory.getLogger(CacheMgt.class);
    private ArrayList<ICache> m_instances = new ArrayList<ICache>();
    /** List of Table Names				*/
    private ArrayList<String>	m_tableNames = new ArrayList<String>();

    /**	Singleton					*/
    private static CacheMgt	s_cache = null;

    /**
     * 	Get Cache Management
     * 	@return Cache Manager
     */
    public static synchronized CacheMgt get()
    {
        if (s_cache == null)
            s_cache = new CacheMgt();
        return s_cache;
    }	//	get

    /**
     *	Private Constructor
     */
    private CacheMgt()
    {
    }	//	CacheMgt

    /**************************************************************************
     * 	Register Cache Instance
     *	@param instance Cache
     *	@return true if added
     */
    @SuppressWarnings("unchecked")
    public synchronized boolean register (ICache instance)
    {
        if (instance == null)
            return false;
        if (instance instanceof CCache)
        {
            String tableName = ((CCache)instance).getName();
            m_tableNames.add(tableName);
        }
        return m_instances.add (instance);
    }	//	register

    /**
     * 	Un-Register Cache Instance
     *	@param instance Cache
     *	@return true if removed
     */
    public boolean unregister (ICache instance)
    {
        if (instance == null)
            return false;
        boolean found = false;
        //	Could be included multiple times
        for (int i = m_instances.size()-1; i >= 0; i--)
        {
            ICache stored = (ICache) m_instances.get(i);
            if (instance.equals(stored))
            {
                m_instances.remove(i);
                found = true;
            }
        }
        return found;
    }	//	unregister

    /**************************************************************************
     * 	Reset All registered Cache
     * 	@return number of deleted cache entries
     */
    public int reset()
    {
        int counter = 0;
        int total = 0;
        for (int i = 0; i < m_instances.size(); i++)
        {
            ICache stored = (ICache) m_instances.get(i);
            if (stored != null && stored.size() > 0)
            {
                log.info(stored.toString());
                total += stored.reset();
                counter++;
            }
        }
        log.info("#" + counter + " (" + total + ")");
        return total;
    }	//	reset

    /**
     * 	Reset registered Cache
     * 	@param tableName table name
     * 	@return number of deleted cache entries
     */
    public int reset (String tableName)
    {
        return reset (tableName, 0);
    }	//	reset

    /**
     * 	Reset registered Cache
     * 	@param tableName table name
     * 	@param Record_ID record if applicable or 0 for all
     * 	@return number of deleted cache entries
     */
    @SuppressWarnings("unchecked")
    public int reset (String tableName, int Record_ID)
    {
        if (tableName == null)
            return reset();
        //	if (tableName.endsWith("Set"))
        //		tableName = tableName.substring(0, tableName.length()-3);
        if (!m_tableNames.contains(tableName))
            return 0;
        //
        int counter = 0;
        int total = 0;
        for (int i = 0; i < m_instances.size(); i++)
        {
            ICache stored = (ICache) m_instances.get(i);
            if (stored != null && stored instanceof CCache)
            {
                CCache cc = (CCache)stored;
                if (cc.getName().startsWith(tableName))		//	reset lines/dependent too
                {
                    //	if (Record_ID == 0)
                    {
                        log.info("(all) - " + stored);
                        total += stored.reset();
                        counter++;
                    }
                }
            }
        }
        log.info(tableName + ": #" + counter + " (" + total + ")");

        return total;
    }	//	reset

    /**
     * 	Total Cached Elements
     *	@return count
     */
    @SuppressWarnings("unchecked")
    public int getElementCount()
    {
        int total = 0;
        for (int i = 0; i < m_instances.size(); i++)
        {
            ICache stored = (ICache) m_instances.get(i);
            if (stored != null && stored.size() > 0)
            {
                log.info(stored.toString());
                if (stored instanceof CCache)
                    total += ((CCache)stored).sizeNoExpire();
                else
                    total += stored.size();
            }
        }
        return total;
    }	//	getElementCount


    /**
     * 	String Representation
     *	@return info
     */
    public String toString ()
    {
        StringBuffer sb = new StringBuffer ("CacheMgt[");
        sb.append("Instances=")
                .append(m_instances.size())
                .append("]");
        return sb.toString ();
    }	//	toString

    /**
     * 	Extended String Representation
     *	@return info
     */
    public String toStringX ()
    {
        StringBuffer sb = new StringBuffer ("CacheMgt[");
        sb.append("Instances=")
                .append(m_instances.size())
                .append(", Elements=")
                .append(getElementCount())
                .append("]");
        return sb.toString ();
    }	//	toString
}
