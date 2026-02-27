package com.gba.eugene.kernel.exceptions;

public class DBException extends SystemException{

    public DBException(Exception e){
        super(e);
    }

    /**
     * Create a new DBException
     * @param msg Message
     */
    public DBException(String msg)
    {
        super(msg);
    }
}
