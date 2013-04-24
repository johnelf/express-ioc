package com.expressioc.scope;

public interface SingletonCache {
    public <T> T getSingletons(Class<T> clazz);
}
