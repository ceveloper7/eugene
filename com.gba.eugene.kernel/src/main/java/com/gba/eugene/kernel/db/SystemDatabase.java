package com.gba.eugene.kernel.db;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Timestamp;

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
     * 	Close
     */
    public void close();

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
     *  Create SQL TO Date statement for Timestamp
     *
     *  @param  time Date to be converted
     *  @param  dayOnly true if time should be set to 00:00:00
     *  @return to date function
     */
    public String TO_DATE (Timestamp time, boolean dayOnly);

    /**
     *  Create SQL for formatted Date, Number
     *
     *  @param  columnName  the column name in the SQL
     *  @param  displayType Display Type
     *  @param  AD_Language 6 character language setting (from Env.LANG_*)
     *
     *  @return TRIM(TO_CHAR(columnName,'999G999G999G990D00','NLS_NUMERIC_CHARACTERS='',.'''))
     *      or TRIM(TO_CHAR(columnName,'TM9')) depending on DisplayType and Language
     *
     **/
    public String TO_CHAR (String columnName, int displayType, String AD_Language);


    /**
     * 	Return number as string for INSERT statements with correct precision
     *	@param number number
     *	@param displayType display Type
     *	@return number as string
     */
    public String TO_NUMBER (BigDecimal number, int displayType);

    /**
     * 	Return string as JSON object for INSERT statements
     *	@param value
     *	@return value as JSON
     */
    public String TO_JSON (String value);

    /**
     *	@return string with right casting for JSON inserts
     */
    public String getJSONCast ();

    /**
     *  String Representation
     *  @return info
     */
    public String toString();

}
