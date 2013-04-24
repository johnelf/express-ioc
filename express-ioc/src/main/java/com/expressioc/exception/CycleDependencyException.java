package com.expressioc.exception;

public class CycleDependencyException extends RuntimeException {
    public CycleDependencyException(Class clazz) {
        super(clazz.getName());
    }
}
