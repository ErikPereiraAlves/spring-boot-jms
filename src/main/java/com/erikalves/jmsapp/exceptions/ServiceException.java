package com.erikalves.jmsapp.exceptions;

public class ServiceException extends Exception {

    private static final long serialVersionUID = 6817611783825305345L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}
