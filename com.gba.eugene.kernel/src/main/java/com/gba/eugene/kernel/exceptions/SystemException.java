package com.gba.eugene.kernel.exceptions;

public class SystemException extends RuntimeException{
    private static final long serialVersionUID = 2340179640558569534L;

    public SystemException(){
        super();
    }

    public SystemException(String message){
        super(message);
    }

    public SystemException(Throwable cause){
        super(cause);
    }
}
