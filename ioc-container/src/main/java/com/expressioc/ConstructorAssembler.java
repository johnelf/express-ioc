package com.expressioc;

import com.expressioc.exception.CycleDependencyException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ConstructorAssembler implements Assembler {
    private ExpressContainer container;

    @Override
    public Assembler setContainer(ExpressContainer container) {
        this.container = container;
        return this;
    }

    public static <T> Constructor<T>[] getConstructorsSortedByArgsCount(Class<T> clazz) {
        Constructor<T>[] constructors = (Constructor<T>[])clazz.getDeclaredConstructors();
        Arrays.sort(constructors, new Comparator<Constructor<?>>() {
            @Override
            public int compare(Constructor<?> constructorA, Constructor<?> constructorB) {
                return constructorB.getParameterTypes().length - constructorA.getParameterTypes().length;
            }
        });

        return constructors;
    }

    @Override
    public <T> T getInstanceBy(Class<T> clazz) {
        Constructor<T>[] constructors = getConstructorsSortedByArgsCount(clazz);
        for (Constructor<T> constructor : constructors) {
            T instance = null;
            try {
                instance = getComponentBy(constructor);
            } catch (CycleDependencyException e) {
                throw e;
            } catch (Exception e) {
            }

            if (instance != null) {
                return instance;
            }
        }

        return null;
    }

    private <T> T getComponentBy(Constructor<T> constructor) throws
            InvocationTargetException, IllegalAccessException, InstantiationException {
        Class[] parameterTypes = constructor.getParameterTypes();

        if (parameterTypes.length == 0) {
            return constructor.newInstance();
        }

        return constructor.newInstance(resolveObjects(parameterTypes));
    }

    private Object[] resolveObjects(Class[] parameterTypes) {
        ArrayList params = new ArrayList(parameterTypes.length);
        for (Class paramClazz : parameterTypes) {
            params.add(container.doGetComponent(paramClazz));
        }

        return params.toArray();
    }
}
