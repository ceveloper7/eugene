package com.gba.eugene.kernel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public final class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    /**
     * @return true if started
     */
    public static synchronized boolean isStarted()
    {
        return (log != null);
    }
}
