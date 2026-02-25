package com.gba.eugene.kernel.util;

import com.gba.eugene.kernel.model.SystemConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Properties;

public final class SystemProperties implements Serializable {
    private static final long serialVersionUID = -8532658745125L;
    private static Logger log = LoggerFactory.getLogger(SystemProperties.class);

    public static final String SYSTEM_PROPERTY_FILE = "system.properties";
    /** Connection Details	*/
    public static final String	P_CONNECTION = "Connection";
    private static final String	DEFAULT_CONNECTION = "";

    /** System properties */
    private static final String[] PROPERTIES = new String[]{
            P_CONNECTION
    };

    /** System properties values */
    private static final String[] VALUES = new String[]{
            DEFAULT_CONNECTION
    };

    private volatile static Properties s_prop = new Properties();
    private static String s_propertyFileName = null;

    private static boolean      s_client = false;
    /** System environment prefix                                       */
    public static final String  ENV_PREFIX = "env.";

    public static final String SYSTEM_HOME = "SYSTEM_HOME";

    public static String getProperty(String key){
        if (key == null) return "";
        String retStr = s_prop.getProperty(key, "");
        if (retStr == null || retStr.isEmpty())
            return "";
        //
        String value = SecureEngine.decrypt(retStr);
        if (value == null)
            return "";
        return value;
    }

    /**
     *  Are we in Client Mode ?
     *  @return true if client
     */
    public static boolean isClient()
    {
        return s_client;
    }   //  isClient

    /**
     *  Set Client Mode
     *  @param client client
     */
    public static void setClient (boolean client)
    {
        s_client = client;
    }   //  setClient

    /**
     *  Get System Home from Environment
     *  @return systemHome or null
     */
    public static String getSystemHome(){
        String env = SystemConstants.getEnvSystemHome();

        if(env == null || env.trim().isEmpty())
            env = SystemConstants.getSystemHome();

        if(env == null || env.trim().isEmpty()){
            String current = isClient() ? System.getProperty("user.home") : System.getProperty("user.dir");

            if (current != null && current.trim().isEmpty()){
                File file = new File(current);
                if (file.exists() && file.canWrite())
                    env = current;
            }
        }

        if (env == null || env.trim().isEmpty())
            env = File.separator + "system";

        return env;
    } // getSystemHome

    public static String getFileName(boolean tryUserHome){
        if (SystemConstants.getPropertyFile() != null)
            return SystemConstants.getPropertyFile();

        String base = null;
        if(tryUserHome && s_client)
            base = System.getProperty("user.home");

        if(!isClient() || base == null || base.isEmpty()){
            String home = getSystemHome();
            if(home != null)
                base = home;
        }

        if (base != null && !base.endsWith(File.separator))
            base += File.separator;
        if (base == null)
            base = "";

        return base + SYSTEM_PROPERTY_FILE;
    }

    public static boolean loadProperties(String fileName){
        boolean loadOk = true;
        boolean firstTime =false;
        s_prop= new Properties();
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(fileName);
            s_prop.load(fis);
        }
        catch (FileNotFoundException ex){
            log.warn(fileName + " not found");
            loadOk = false;
        }
        catch (Exception ex){
            log.error(fileName + " - " + ex.toString());
            loadOk = false;
        }
        catch(Throwable t){
            log.error(fileName + " - " + t.toString());
            loadOk = false;
        }
        finally {
            if(fis != null){
                try{
                    fis.close();
                }
                catch (Exception e){}
            }
        }

        if(!loadOk)
            firstTime = true;

        return firstTime;
    }

    public static void loadProperties(boolean reload){
        if(reload || s_prop.isEmpty())
            loadProperties(getFileName(s_client));
    }
}
