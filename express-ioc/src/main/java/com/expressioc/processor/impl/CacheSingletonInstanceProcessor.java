package com.expressioc.processor.impl;

import com.expressioc.annotation.Singleton;
import com.expressioc.scope.SingletonCache;
import com.expressioc.processor.AssembleProcessor;

import java.util.HashMap;

import static com.google.common.collect.Maps.newHashMap;

public class CacheSingletonInstanceProcessor implements AssembleProcessor, SingletonCache {
    private HashMap<Class, Object> cache = newHashMap();

    @Override
    public void initBeforeGetComponentFromContainer() {
    }

    @Override
    public void beforeAssemble(Class clazz) {
    }

    @Override
    public Object postAssemble(Class clazz, Object instance) {
        boolean isSingletonInstance = instance != null && clazz.isAnnotationPresent(Singleton.class);

        if (isSingletonInstance) {
            cache.put(clazz, instance);
        }

        return instance;
    }


    @Override
    public <T> T getSingletons(Class<T> clazz) {
        return (T) cache.get(clazz);
    }
}
