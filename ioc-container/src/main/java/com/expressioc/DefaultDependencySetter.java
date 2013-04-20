package com.expressioc;

import com.expressioc.exception.SetterInjectionException;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class DefaultDependencySetter implements DependencySetter{
    private ExpressContainer container;

    @Override
    public DependencySetter setContainer(ExpressContainer container) {
        this.container = container;
        return this;
    }

    @Override
    public void setDependencies(Object instance) {
        Method[] clazzMethods = instance.getClass().getMethods();
        for (Method method : clazzMethods) {
            if (isSetterMethod(method)) {
                try {
                    method.invoke(instance, resolveObjects(method.getParameterTypes()));
                } catch (Exception e) {
                    throw new SetterInjectionException(e);
                }
            }
        }
    }

    private boolean isSetterMethod(Method method) {
        //TODO: refactor to using regex
        return method.getName().startsWith("set");
    }

    private Object[] resolveObjects(Class[] parameterTypes) {
        ArrayList params = new ArrayList(parameterTypes.length);
        for (Class paramClazz : parameterTypes) {
            params.add(container.doGetComponent(paramClazz));
        }

        return params.toArray();
    }
}
