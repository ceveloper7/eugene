package com.gba.eugene.kernel.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {
    private static final Logger log = LoggerFactory.getLogger(Database.class);

    /** PostgreSQL ID */
    public static String DATABASE_POSTGRESQL = "PostgreSQL";

    /** Connection Timeout in seconds   */
    public static int CONNECTION_TIMEOUT = 10;
    /** Default Port            */
    public static final int DATABASE_POSTGRESQL_DEFAULT_PORT = 5432;

    /** Supported Databases */
    public static String[] DATABASE_NAMES = new String[] {
            DATABASE_POSTGRESQL
    };

    /** Database Classes */
    protected static Class<?>[] DATABASE_CLASSES = new Class[]{
            Database_PostgresSQL.class
    };

    // Get Database by Database ID
    public static SystemDatabase getDatabase(String type) throws Exception{
        SystemDatabase db = null;
        for(int i = 0; i < DATABASE_NAMES.length; i++){
            if(Database.DATABASE_NAMES[i].equals(type)){
                db = (SystemDatabase) Database.DATABASE_CLASSES[i].newInstance();
                break;
            }
        }
        return db;
    }

    // get Database driver by url string
    public static SystemDatabase getDatabaseFromURL(String url){
        if(url == null){
            log.error("No Database URL");
            return null;
        }

        if(url.indexOf("postgresql") != -1)
            return new Database_PostgresSQL();

        log.error("No Database for: " + url);
        return null;
    }
}
