package com.expressioc;

import com.expressioc.exception.AssembleComponentFailedException;
import com.expressioc.exception.CycleDependencyException;
import com.expressioc.parameters.Parameter;
import com.expressioc.utility.ClassUtility;

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
        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
        Arrays.sort(constructors, new Comparator<Constructor<?>>() {
            @Override
            public int compare(Constructor<?> constructorA, Constructor<?> constructorB) {
                return constructorB.getParameterTypes().length - constructorA.getParameterTypes().length;
            }
        });

        return constructors;
    }

    @Override
    public <T> T getInstanceBy(Class<T> clazz, Parameter... arguments) {
        if (arguments != null && arguments.length > 0) {
            return constructBy(clazz, arguments);
        }

        return constructByMostSuitableConstructor(clazz);
    }

    private <T> T constructByMostSuitableConstructor(Class<T> clazz) {
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

    private <T> T constructBy(Class<T> clazz, Parameter[] arguments) {
        Constructor constructor = getConstructorsOfArgsCountEqualTo(clazz, arguments.length);
        Class[] parameterTypes = constructor.getParameterTypes();

        ArrayList<Object> args = new ArrayList<Object>(arguments.length);
        for (int i = 0; i < parameterTypes.length; i++) {
            Class paramType = parameterTypes[i];
            String argValue = arguments[i] != null ? arguments[i].getValue() : "";

            args.add(getArgFrom(paramType, argValue));
        }

        try {
            return (T) constructor.newInstance(args.toArray());
        } catch (Exception e) {
            throw new AssembleComponentFailedException(e);
        }
    }

    private <T> T getArgFrom(Class<T> type, String value) {
        if (ClassUtility.isBasicType(type)) {
            try {
                return ClassUtility.assembleParameter(value, type);
            } catch (Exception e) {
                return null;
            }
        }

        return container.getComponent(type);
    }


    private <T> Constructor getConstructorsOfArgsCountEqualTo(Class<T> clazz, int length) {
        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
        for (Constructor constructor : constructors) {
            if (constructor.getParameterTypes().length == length) {
                return constructor;
            }
        }

        throw new AssembleComponentFailedException("Constructor suitable for provided parameters not found.");
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
