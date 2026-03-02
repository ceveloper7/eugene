package com.gba.eugene.kernel;

import com.gba.eugene.kernel.db.DatabaseConnection;
import com.gba.eugene.kernel.util.DB;
import com.gba.eugene.kernel.util.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public final class App {

    private static Logger log = null;
    static public String	MAIN_VERSION	= "Release 1.0.0";
    /** Detail Version as date      Used for Client/Server		*/
    static public String	DATE_VERSION	= "2026-03-01";

    /**
     *  Get Product Version
     *  @return Application Version
     */
    public static String getVersion()
    {
        return MAIN_VERSION + " @ " + DATE_VERSION;
    }   //  getVersion

    /**
     * @return true if started
     */
    public static synchronized boolean isStarted()
    {
        return (log != null);
    }

    /**
     * 	Startup System Environment.<br/>
     * 	Automatically called for Server connections. <br/>
     *	@param isClient true if client connection
     *  @return successful startup
     */
    public static boolean startupEnvironment (boolean isClient)
    {
        startup(isClient);		//	returns if already initiated
        if (!DB.isConnected())
        {
            System.out.println("No database");
            log.error("No Database");
            return false;
        }
        else{
            System.out.println("Database connected - OK");
            log.info("Database connected - OK");
        }

        return true;
    }

    public static synchronized boolean startup (boolean isClient)
    {
        //	Already started
        if (log != null)
            return true;

        SystemProperties.setClient (isClient);
        log = LoggerFactory.getLogger(App.class);
        SystemProperties.loadProperties(false);
        DB.setDBTarget(DatabaseConnection.get());

        if (isClient)		//	don't test connection
            return false;	//	need to call

        return startupEnvironment(isClient);
    }

    public static void main(String[] args){
        startup(true);
        System.out.println(DB.getSQLValueString(null, "SELECT Version FROM AD_System"));
    }
}
