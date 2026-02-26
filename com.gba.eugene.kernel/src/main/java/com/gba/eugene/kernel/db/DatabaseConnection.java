package com.gba.eugene.kernel.db;

import com.gba.eugene.kernel.util.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

public class DatabaseConnection implements Serializable, Cloneable {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConnection.class);

    /** Connection */
    private volatile static  DatabaseConnection s_cc = null;

    /** Name of Connection  */
    private String 		m_name = "Standard";

    /** Application Host    */
    private String 		m_apps_host = "MyAppsServer";

    /** Database Type       */
    private String 		m_type = "";

    /** Database Host       */
    private String 		m_db_host = "MyDBServer";
    /** Database Port       */
    private int m_db_port = 0;
    /** Database name       */
    private String 		m_db_name = "MyDBName";

    /** DB User name        */
    private String 		m_db_uid = "system";
    /** DB User password    */
    private String 		m_db_pwd = "system";

    /** Database */
    private SystemDatabase m_db = null;

    private Exception m_dbException = null;

    /** Database Connection 	*/
    private boolean 	m_okDB = false;

    /** Info                */
    private String[] 	m_info = new String[2];

    /**	Server Version		*/
    private String 		m_version = null;

    /** DataSource      	*/
    private DataSource m_ds = null;
    /** DB Info				*/
    private String		m_dbInfo = null;

    private int m_webPort;

    public DatabaseConnection(String host){
        if (host != null){
            m_apps_host = host;
            m_db_host = host;
        }
    }

    public synchronized static DatabaseConnection get(){
        if (s_cc == null){
            String attributes = SystemProperties.getProperty(SystemProperties.P_CONNECTION);
            s_cc = new DatabaseConnection(null);
            s_cc.setAttribute(attributes);
            log.info(s_cc.toString());
        }

        return s_cc;
    }

    public static DatabaseConnection get(String type, String db_host, int db_port, String db_name){
        return get (type, db_host, db_port, db_name, null, null);
    }

    public static DatabaseConnection get(String type, String db_host, int db_port, String db_name, String db_uid, String db_pwd){
        DatabaseConnection cc = new DatabaseConnection(db_host);
        cc.setAppsHost(db_host);
        cc.setType(type);
        cc.setDbHost(db_host);
        cc.setDbPort(db_port);
        cc.setDbName(db_name);

        if(db_uid != null)
            cc.setDbUid(db_uid);

        if(db_pwd != null)
            cc.setDbPwd(db_pwd);

        return cc;
    }

    /**
     *  Get Name
     *  @return connection name
     */
    public String getName ()
    {
        return m_name;
    }

    /**
     *  Set Name
     *  @param name connection name
     */
    public void setName (String name)
    {
        m_name = name;
    }	//  setName

    /**
     *  Set Name
     */
    public void setName ()
    {
        m_name = toString ();
    } 	//  setName

    /**
     *  Get Application Host
     *  @return apps host
     */
    public String getAppsHost ()
    {
        return m_apps_host;
    }

    /**
     *  Set Application Host
     *  @param apps_host apps host
     */
    public void setAppsHost (String apps_host)
    {
        m_apps_host = apps_host;
        m_name = toString ();
    }

    /**
     * @return web port
     */
    public int getWebPort()
    {
        return m_webPort;
    }

    /**
     * set web port
     * @param webPort
     */
    public void setWebPort(int webPort)
    {
        m_webPort = webPort;
    }

    /**
     * Set Web Port
     * @param webPortString web port as String
     */
    public void setWebPort (String webPortString)
    {
        try
        {
            if (webPortString == null || webPortString.length() == 0)
                ;
            else
                setWebPort (Integer.parseInt (webPortString));
        }
        catch (Exception e)
        {
            log.error(e.toString ());
        }
    }

    /**
     *  Get Database Host name
     *  @return db host name
     */
    public String getDbHost ()
    {
        return m_db_host;
    }	//	getDbHost

    /**
     *  Set Database host name
     *  @param db_host db host
     */
    public void setDbHost (String db_host)
    {
        m_db_host = db_host;
        m_name = toString ();
        m_okDB = false;
    }	//	setDbHost

    /**
     *  Get Database Name (Service Name)
     *  @return db name
     */
    public String getDbName ()
    {
        return m_db_name;
    }	//	getDbName

    /**
     *  Set Database Name (Service Name)
     *  @param db_name db name
     */
    public void setDbName (String db_name)
    {
        m_db_name = db_name;
        m_name = toString ();
        m_okDB = false;
    }	//	setDbName

    /**
     * 	Get DB Port
     * 	@return port
     */
    public int getDbPort ()
    {
        return m_db_port;
    }	//	getDbPort

    /**
     * Set DB Port
     * @param db_port db port
     */
    public void setDbPort (int db_port)
    {
        m_db_port = db_port;
        m_okDB = false;
    }	//	setDbPort

    /**
     * Set DB Port
     * @param db_portString db port as String
     */
    public void setDbPort (String db_portString)
    {
        try
        {
            if (db_portString == null || db_portString.length() == 0)
                ;
            else
                setDbPort (Integer.parseInt (db_portString));
        }
        catch (Exception e)
        {
            log.error(e.toString ());
        }
    } 	//  setDbPort

    /**
     *  Get Database Password
     *  @return db password
     */
    public String getDbPwd ()
    {
        return m_db_pwd;
    }	//	getDbPwd

    /**
     *  Set DB password
     *  @param db_pwd db user password
     */
    public void setDbPwd (String db_pwd)
    {
        m_db_pwd = db_pwd;
        m_okDB = false;
    }	//	setDbPwd

    /**
     *  Get Database User
     *  @return db user
     */
    public String getDbUid ()
    {
        return m_db_uid;
    }	//	getDbUid

    /**
     *  Set Database User
     *  @param db_uid db user id
     */
    public void setDbUid (String db_uid)
    {
        m_db_uid = db_uid;
        m_name = toString ();
        m_okDB = false;
    }	//	setDbUid

    /**
     *  Has DB data source
     *  @return true if DataSource exists
     */
    public boolean isDataSource ()
    {
        return m_ds != null;
    } 	//	isDataSource

    public boolean setDataSource(){
        if(m_ds == null && SystemProperties.isClient()){
            SystemDatabase getDB = getDatabase();
            if(getDB != null)
                m_ds = getDB.getDataSource(this);
        }
        return m_ds != null;
    }

    public boolean setDataSource(DataSource ds){
        if(ds == null && m_ds != null)
            getDatabase().close();
        m_ds = ds;
        return m_ds != null;
    }

    /**
     *  Get DB data source
     *  @return DataSource
     */
    public DataSource getDataSource ()
    {
        return m_ds;
    } 	//	getDataSource

    /**
     *  Is PostgreSQL DB
     *  @return true if PostgreSQL
     */
    public boolean isPostgreSQL ()
    {
        return Database.DATABASE_POSTGRESQL.equals (m_type);
    } 	//  isPostgreSQL

    /**
     *  Set Database Type and default settings.
     *  Checked against installed databases
     *  @param type database Type, e.g. Database.DataBase_PostgresSQL
     */
    public void setType (String type)
    {
        try{
            if (Database.getDatabase(type) != null)
            {
                m_type = type;
                m_okDB = false;
            }

            //  PostgreSQL
            if (isPostgreSQL ())
            {
                if (getDbPort () != Database.DATABASE_POSTGRESQL_DEFAULT_PORT)
                    setDbPort (Database.DATABASE_POSTGRESQL_DEFAULT_PORT);
            }
        }
        catch (Exception ex){
            log.error("There is no Database");
        }
    }


    /**
     *  Short String representation
     *  @return appsHost{dbHost-dbName-uid}
     */
    public String toString ()
    {
        StringBuilder sb = new StringBuilder (m_apps_host);
        sb.append ("{").append (m_db_host)
                .append ("-").append (m_db_name)
                .append ("-").append (m_db_uid)
                .append ("}");
        return sb.toString ();
    } 	//  toString

    /**
     *  Get Connection URL
     *  @return connection URL
     */
    public String getConnectionURL ()
    {
        getDatabase (); //  updates m_db
        if (m_db != null)
            return m_db.getConnectionURL (this);
        else
            return "";
    } 	//  getConnectionURL

    /**
     *  Get Database Adapter
     *  @return database adapter instance
     */
    public SystemDatabase getDatabase (){
        //  different driver
        if (m_db != null && !m_db.getName ().equals (m_type))
            m_db = null;

        if (m_db == null){
            try{
                m_db = Database.getDatabase(m_type);
                if (m_db != null)		//	test class loader ability
                    m_db.getDataSource(this);
            }
            catch (NoClassDefFoundError ee){
                System.err.println("Environment Error - Check idempiere.properties - " + ee);
                System.exit(1);
            }
            catch (Exception e){
                log.error(e.toString());
            }
        }
        return m_db;
    }

    /**
     *  Get Connection.
     * 	Sets {@link #m_dbException}.
     *  @param autoCommit true if autocommit connection
     *  @param transactionIsolation transaction isolation level
     *  @return Connection
     */
    public Connection getConnection (boolean autoCommit, int transactionIsolation){
        Connection conn = null;
        m_dbException = null;
        m_okDB = false;
        //
        getDatabase (); //  updates m_db
        if (m_db == null)
        {
            m_dbException = new IllegalStateException("No Database Connector");
            return null;
        }

        try{
            conn = m_db.getCachedConnection(this, autoCommit, transactionIsolation);
            // Verify Connection
            if (conn != null)
            {
                if (conn.getTransactionIsolation() != transactionIsolation)
                    conn.setTransactionIsolation (transactionIsolation);
                if (conn.getAutoCommit() != autoCommit)
                    conn.setAutoCommit (autoCommit);
                m_okDB = true;
            }

        }
        catch (Exception ex)
        {
            m_dbException = ex;
            System.err.println(getConnectionURL() + " - " + ex.getLocalizedMessage());
        }
        return conn;
    }

    public String getDBInfo(){
        if(m_dbInfo != null)
            return m_dbInfo;
        StringBuilder sb = new StringBuilder ();
        Connection conn = getConnection (true, Connection.TRANSACTION_READ_COMMITTED);
        if(conn != null){
            try{
                DatabaseMetaData dbmd = conn.getMetaData();
                sb.append(dbmd.getDatabaseProductVersion())
                        .append(";").append(dbmd.getDriverVersion());
                if (isDataSource())
                    sb.append(";DS");
                conn.close ();
                m_dbInfo = sb.toString ();
            }
            catch (Exception ex){
                log.error(ex.toString());
            }
        }
        conn = null;
        return sb.toString();
    }


    /**
     * 	Detail Info
     *	@return info
     */
    public String toStringDetail ()
    {
        StringBuilder sb = new StringBuilder (m_apps_host);
        sb.append ("{").append (m_db_host)
                .append ("-").append (m_db_name)
                .append ("-").append (m_db_uid)
                .append ("}");
        //
        Connection conn = getConnection (true, Connection.TRANSACTION_READ_COMMITTED);
        if (conn != null)
        {
            try
            {
                DatabaseMetaData dbmd = conn.getMetaData ();
                sb.append("\nDatabase=" + dbmd.getDatabaseProductName ()
                        + " - " + dbmd.getDatabaseProductVersion());
                sb.append("\nDriver  =" + dbmd.getDriverName ()
                        + " - " + dbmd.getDriverVersion ());
                if (isDataSource())
                    sb.append(" - via DS");
                conn.close ();
            }
            catch (Exception e)
            {
            }
        }
        conn = null;
        return sb.toString ();
    } 	//  toStringDetail

    public String toStringLong(){
        StringBuilder sb = new StringBuilder ("CConnection[");
        sb.append ("name=").append(escape(m_name))
                .append(",AppsHost=").append (escape(m_apps_host))
                .append(",WebPort=").append (m_webPort)
                .append(",type=").append (escape(m_type))
                .append(",DBhost=").append (escape(m_db_host))
                .append(",DBport=").append (m_db_port)
                .append(",DBname=").append (escape(m_db_name))
                .append(",UID=").append (escape(m_db_uid))
                .append(",PWD")
                .append("]");
        return sb.toString();
    }

    /**
     * Use html like escape sequence to escape = and ,
     * @param value
     * @return escape value
     */
    private String escape(String value) {
        if (value == null)
            return null;

        // use html like escape sequence to escape = and ,
        value = value.replace("=", "&eq;");
        value = value.replace(",", "&comma;");
        return value;
    }

    /**
     * @param value
     * @return un-escape value
     * @see DatabaseConnection#escape(String)
     */
    private String unescape(String value) {
        value = value.replace("&eq;", "=");
        value = value.replace("&comma;", ",");
        return value;
    }

    private void setAttribute(String attributes){
        try{
            attributes = attributes.substring(attributes.indexOf("[")+1, attributes.length() - 1);
            String[] pairs= attributes.split("[,]");
            for(String pair : pairs){
                String[] pairComponents = pair.split("[=]");
                String key = pairComponents[0];
                String value = pairComponents.length == 2 ? unescape(pairComponents[1]) : "";
                if ("name".equalsIgnoreCase(key))
                {
                    setName(value);
                }
                else if ("AppsHost".equalsIgnoreCase(key))
                {
                    setAppsHost(value);
                }
                else if ("type".equalsIgnoreCase(key))
                {
                    setType(value);
                }
                else if ("DBhost".equalsIgnoreCase(key))
                {
                    setDbHost(value);
                }
                else if ("DBport".equalsIgnoreCase(key))
                {
                    setDbPort(value);
                }
                else if ("DbName".equalsIgnoreCase(key))
                {
                    setDbName(value);
                }
                else if ("UID".equalsIgnoreCase(key))
                {
                    setDbUid(value);
                }
                else if ("PWD".equalsIgnoreCase(key))
                {
                    setDbPwd(value);
                }
                else if ("WebPort".equalsIgnoreCase(key))
                {
                    setWebPort(value);
                }
            }
        }
        catch (Exception e){
            log.info(attributes + " - " + e.toString());
        }
    }
}
