package com.gba.eugene.kernel.model;

import com.gba.eugene.kernel.util.SystemProperties;

public class SystemConstants {

    private static final String PropertyFile = "PropertyFile";
    private static final String env_SYSTEM_HOME = SystemProperties.ENV_PREFIX + SystemProperties.SYSTEM_HOME;
    private static final String SYSTEM_HOME = SystemProperties.SYSTEM_HOME;
    private static final String com_gba_eugene_postgresql_URLParameters = "com.gba.eugene.postgresql.URLParameters";

    /**
     * PropertyFile allows to define a PropertyFile to use instead of the default $HOME/system.properties
     * @return
     */
    public static String getPropertyFile() {
        return System.getProperty(PropertyFile);
    }

    /**
     * env.SYSTE_HOME to define the home of App server instance
     * @return
     */
    public static String getEnvSystemHome() {
        return System.getProperty(env_SYSTEM_HOME);
    }

    /**
     * SYSTEM_HOME to define the home of App server instance
     * @return
     */
    public static String getSystemHome() {
        return System.getProperty(SYSTEM_HOME);
    }

    /**
     * com.gba.eugene.postgresql.URLParameters allows to define additional URL parameters to be passed to
     *   the JDBC connection in PostgreSQL
     * @return
     */
    public static String getPostgresqlURLParameters() {
        return System.getProperty(com_gba_eugene_postgresql_URLParameters);
    }
}
