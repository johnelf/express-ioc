package com.expressioc.provider.impl;

import com.expressioc.provider.InstanceAssembler;

import java.util.HashMap;
import java.util.Map;

public class DefaultInstanceAssembler extends InstanceAssembler{
    private Map<Class, Object> instancesMap = new HashMap<Class, Object>();

    @Override
    public boolean feedAssembler(Class clazz, Object instance) {
        instancesMap.put(clazz, instance);
        return true;
    }

    @Override
    public Object getInstanceBy(Class clazz) {
        return instancesMap.get(clazz);
    }
}
