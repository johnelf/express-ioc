package com.expressioc;

public interface Container {
    public <T> T getComponent(Class<T> clazz);
}