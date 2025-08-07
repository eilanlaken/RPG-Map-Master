package com.heavybox.jtix.application;

public class ApplicationException extends RuntimeException {

    public static final String APPLICATION_STARTUP_EXAMPLE = ""; // TODO

    public ApplicationException(String msg) {
        super(msg + "\n See example: \n" + APPLICATION_STARTUP_EXAMPLE);
    }

}
