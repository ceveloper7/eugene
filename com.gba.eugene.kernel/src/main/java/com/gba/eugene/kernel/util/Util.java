package com.gba.eugene.kernel.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
    private static Logger log = LoggerFactory.getLogger(Util.class);

    /**
     * Is String Empty or null
     * @param str string
     * @return true if str is empty or null
     */
    public static boolean isEmpty (String str)
    {
        return isEmpty(str, false);
    }	//	isEmpty

    /**
     * Is String Empty or null
     * @param str string
     * @param trimWhitespaces trim whitespaces
     * @return true if str is empty or null
     */
    public static boolean isEmpty (String str, boolean trimWhitespaces)
    {
        if (str == null)
            return true;
        if (trimWhitespaces)
            return str.trim().isEmpty();
        else
            return str.isEmpty();
    }	//	isEmpty
}
