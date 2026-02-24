package com.gba.eugene.kernel.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

public interface SystemDatabase {

    /**
     *  Get Database Name
     *  @return database short name
     */
    public String getName();

    /**
     *  Get Database Description
     *  @return database long name and version
     */
    public String getDescription();

    /**
     *  Get and register Database Driver
     *  @return Driver
     *  @throws SQLException
     */
    public Driver getDriver() throws SQLException;


    /**
     *  Get Standard JDBC Port
     *  @return standard port
     */
    public int getStandardPort();

    /**
     * 	Create DataSource
     *	@param connection connection
     *	@return data source
     */
    public DataSource getDataSource(DatabaseConnection connection);

    /**
     * 	Get Cached Connection on Server
     *	@param connection info
     *  @param autoCommit true if autocommit connection
     *  @param transactionIsolation transaction isolation level
     *	@return connection or null
     *  @throws Exception
     */
    public Connection getCachedConnection (DatabaseConnection connection,
                                           boolean autoCommit, int transactionIsolation) throws Exception;


    /**
     *  Get Database Connection String
     *  @param connection Connection Descriptor
     *  @return connection String
     */
    public String getConnectionURL(DatabaseConnection connection);

    /**
     * 	Get Connection URL
     *	@param dbHost db Host
     *	@param dbPort db Port
     *	@param dbName db Name
     *	@param userName user name
     *	@return url
     */
    public String getConnectionURL (String dbHost, int dbPort, String dbName,
                                    String userName);

    /**
     *  Get Database Connection URL
     *  @param connectionURL Connection URL
     *  @param userName user name
     *  @return connection URL
     */
    public String getConnectionURL (String connectionURL, String userName);

    /**
     * 	Get Driver Connection
     *	@param dbUrl URL
     *	@param dbUid user
     *	@param dbPwd password
     *	@return connection
     *	@throws SQLException
     */
    public Connection getDriverConnection (String dbUrl, String dbUid, String dbPwd)
            throws SQLException;

    /**
     * 	Get JDBC Catalog
     *	@return catalog
     */
    public String getCatalog();

    /**
     * 	Get JDBC Schema
     *	@return schema
     */
    public String getSchema();

    /**
     *  Supports BLOB
     *  @return true if BLOB is supported
     */
    public boolean supportsBLOB();

    /**
     *  String Representation
     *  @return info
     */
    public String toString();

}
