package com.expressioc;

import java.util.HashMap;
import java.util.Map;

public class BeanFactory {
    private Map<Class, Class> context = new HashMap<Class, Class>();

    public Map<Class, Class> getContext() {
        return context;
    }

    public <T> Class getBean(Class<?> classType) {
        return context.get(classType);
    }
}
