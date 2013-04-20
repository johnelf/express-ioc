package com.expressioc.assembler.impl;

import com.expressioc.assembler.CacheObjectAssembler;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class InstanceInjectionAssembler extends CacheObjectAssembler {
    private Map<Class, Object> instancesMap = new HashMap<Class, Object>();

    @Override
    public boolean feedAssembler(Class clazz, Object instance) {
        checkArgument(clazz.isInstance(instance), "Expect: clazz.isInstance(instance)", clazz, instance);

        instancesMap.put(clazz, instance);
        return true;
    }

    @Override
    public <T> T getInstanceBy(Class<T> clazz) {
        return (T)instancesMap.get(clazz);
    }
}
