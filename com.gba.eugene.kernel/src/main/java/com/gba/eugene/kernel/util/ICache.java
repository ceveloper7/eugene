package com.gba.eugene.kernel.util;

public interface ICache {

    /**
     *	Reset Cache
     *	@return number of items reset
     */
    public int reset();

    /**
     * 	Get Size of Cache
     *	@return number of items
     */
    public int size();
}
