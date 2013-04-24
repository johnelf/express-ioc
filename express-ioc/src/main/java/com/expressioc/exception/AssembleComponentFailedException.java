package com.expressioc.exception;

public class AssembleComponentFailedException extends RuntimeException {
    public AssembleComponentFailedException(String s) {
        super(s);
    }

    public AssembleComponentFailedException(Exception e) {
        super(e);
    }

    public AssembleComponentFailedException(Class clazz) {
        super(clazz.getName());
    }
}
