package com.gba.eugene.kernel.db;

import com.gba.eugene.kernel.exceptions.DBException;
import com.gba.eugene.kernel.model.SystemConstants;
import com.gba.eugene.kernel.util.SystemProperties;
import com.gba.eugene.kernel.util.Util;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


public class Database_PostgresSQL implements SystemDatabase{

    private static final Logger log = LoggerFactory.getLogger(Database_PostgresSQL.class);

    private String m_userName = null;
    private String m_connectionURL = null;
    private String m_dbName = null;

    public Database_PostgresSQL(){}

    /** Driver                  */
    private org.postgresql.Driver   s_driver = null;

    /** Driver class            */
    public static final String DRIVER = "org.postgresql.Driver";

    /** Default Port            */
    public static final int         DATABASE_DEFAULT_PORT = 5432;

    private volatile HikariDataSource m_ds;
    private static final String POOL_PROPERTIES = "hikaricp.properties";

    /** Boolean to indicate the PostgreSQL connection pool is either initializing or initialized.*/
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    /** Latch which can be used to wait for initialization completion. */
    private final CountDownLatch initializedLatch = new CountDownLatch(1);

    private String getPoolPropertiesFile(){
        String base = SystemProperties.getSystemHome();
        if (base != null && !base.endsWith(File.separator)) {
            base += File.separator;
        }

        return base + getName() + File.separator + POOL_PROPERTIES;
    }

    private Properties getPoolProperties(){
        File userPropertyFile = new File(getPoolPropertiesFile());
        URL propertyFileURL = null;

        if (userPropertyFile.exists() && userPropertyFile.canRead())
        {
            try {
                propertyFileURL = userPropertyFile.toURI().toURL();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(propertyFileURL == null){
            throw new DBException(new Exception());
        }

        Properties poolProperties = new Properties();
        try(InputStream propertyFileInputStream = propertyFileURL.openStream()){
            poolProperties.load(propertyFileInputStream);
        }catch (Exception e){
            throw new DBException(e);
        }

        //auto create property file at home folder from default config
        if (!userPropertyFile.exists())
        {
            try {
                Path directory = userPropertyFile.toPath().getParent();
                Files.createDirectories(directory);

                try (InputStream propertyFileInputStream = propertyFileURL.openStream()) {
                    Files.copy(propertyFileInputStream, userPropertyFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return poolProperties;
    }

    @Override
    public String getName() {
        return Database.DATABASE_POSTGRESQL;
    }

    @Override
    public String getDescription() {
        try{
            if (s_driver == null)
                getDriver();
        }
        catch (Exception ex){}

        if (s_driver == null)
            return s_driver.toString();

        return "There is no Driver";
    }

    @Override
    public java.sql.Driver getDriver() throws SQLException {
        if (s_driver == null){
            s_driver = new org.postgresql.Driver();
            DriverManager.registerDriver(s_driver);
            DriverManager.setLoginTimeout(Database.CONNECTION_TIMEOUT);
        }
        return s_driver;
    }

    @Override
    public int getStandardPort() {
        return DATABASE_DEFAULT_PORT;
    }

    @Override
    public String getConnectionURL(DatabaseConnection connection) {
        StringBuilder sb = new StringBuilder("jdbc:postgresql://")
                .append(connection.getDbHost())
                .append(":").append(connection.getDbPort())
                .append("/").append(connection.getDbName())
                .append("?encoding=UNICODE&tcpKeepAlive=true");

        String urlParameters = SystemConstants.getPostgresqlURLParameters();
        if(!Util.isEmpty(urlParameters))
            sb.append("&").append(urlParameters);

        return sb.toString();
    }

    @Override
    public String getConnectionURL(String dbHost, int dbPort, String dbName, String userName) {
        StringBuilder sb = new StringBuilder("jdbc:postgresql://")
                .append(dbHost)
                .append(":").append(dbPort)
                .append("/").append(dbName);

        String urlParameters = SystemConstants.getPostgresqlURLParameters();
        if (!Util.isEmpty(urlParameters)) {
            sb.append("?").append(urlParameters);
        }

        return sb.toString();
    }

    @Override
    public String getConnectionURL(String connectionURL, String userName) {
        m_userName = userName;
        m_connectionURL = connectionURL;
        return m_connectionURL;
    }

    @Override
    public Connection getDriverConnection(String dbUrl, String dbUid, String dbPwd) throws SQLException {
        getDriver();
        return DriverManager.getConnection(dbUrl, dbUid, dbPwd);
    }

    @Override
    public String getCatalog() {
        if (m_dbName != null)
            return m_dbName;

        return null;
    }

    @Override
    public String getSchema() {
        return "gba";
    }

    @Override
    public boolean supportsBLOB() {
        return true;
    }

    /**
     * Allows the connection pool to be lazily initialized. While it might be preferable to do
     * this once upon initialization of this class the current design of iDempiere makes this
     * hard.
     *
     * Calling this method will block until the pool is configured. This does NOT mean it will
     * block until a database connection has been setup.
     *
     * @param connection
     */
    private void ensureInitialized(DatabaseConnection connection){
        if (!initialized.compareAndSet(false, true)) {
            try {
                initializedLatch.await();
            } catch (InterruptedException e) {
                return;
            }
        }

        try {
            Properties poolProperties = getPoolProperties();
            // Do not override values which might have been read from the users
            // hikaricp.properties file.
            if(!poolProperties.containsKey("jdbcUrl")) {
                poolProperties.put("jdbcUrl", getConnectionURL(connection));
            }
            if (!poolProperties.containsKey("username")) {
                poolProperties.put("username", connection.getDbUid());
            }
            if (!poolProperties.containsKey("password")) {
                poolProperties.put("password", connection.getDbPwd());
            }

            HikariConfig hikariConfig = new HikariConfig(poolProperties);
            hikariConfig.setDriverClassName(DRIVER);
            m_ds = new HikariDataSource(hikariConfig);

            m_connectionURL = m_ds.getJdbcUrl();

            initializedLatch.countDown();
        }
        catch (Exception ex) {
            throw new IllegalStateException("Could not initialise Hikari Datasource", ex);
        }
    }

    /**
     * 	Create DataSource (Client)
     *	@param connection connection
     *	@return data dource
     */
    public DataSource getDataSource(DatabaseConnection connection)
    {
        ensureInitialized(connection);
        return m_ds;
    }

    /**
     * 	Get Cached Connection
     *	@param connection connection
     *	@param autoCommit auto commit
     *	@param transactionIsolation trx isolation
     *	@return Connection
     *	@throws Exception
     */
    public Connection getCachedConnection (DatabaseConnection connection, boolean autoCommit, int transactionIsolation)
            throws Exception{
        Connection conn = null;

        if (m_ds == null)
            getDataSource(connection);

        // If HikariCP has no available free connection this call will block until either
        // a connection becomes available or the configured 'connectionTimeout' value is
        // reached (after which a SQLException is thrown).
        conn = m_ds.getConnection();

        if (conn.getTransactionIsolation() != transactionIsolation)
        {
            conn.setTransactionIsolation(transactionIsolation);
        }
        if (conn.getAutoCommit() != autoCommit)
        {
            conn.setAutoCommit(autoCommit);
        }
        return conn;
    }
}
