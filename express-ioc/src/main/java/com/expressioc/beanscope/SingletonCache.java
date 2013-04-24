package com.expressioc.beanscope;

public interface SingletonCache {
    public <T> T getSingletons(Class<T> clazz);
}
