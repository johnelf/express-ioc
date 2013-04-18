package com.expressioc;

import com.expressioc.exception.AssembleComponentFailedException;
import com.expressioc.exception.CycleDependencyException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ExpressContainer implements Container{
    private Container parent;
    private Map<Class, Class> implementationsMap = new HashMap<Class, Class>();
    private Map<Class, Object> instancesMap = new HashMap<Class, Object>();
    private Set<Class> classesUnderConstruct = new HashSet<Class>();

    public void setParent(ExpressContainer parent) {
        this.parent = parent;
    }

    @Override
    public <T> T getComponent(Class<T> clazz) {
        this.classesUnderConstruct.clear();
        return doGetComponent(clazz);
    }

    private <T> T doGetComponent(Class<T> clazz) {
        Object targetInstance = instancesMap.get(clazz);
        if (targetInstance != null) {
            return (T) targetInstance;
        }

        Class concreteClass = implementationsMap.get(clazz);
        Class targetClass = concreteClass == null ? clazz : concreteClass;

        if (classesUnderConstruct.contains(targetClass)) {
            throw new CycleDependencyException();
        }

        classesUnderConstruct.add(targetClass);

        Constructor<T>[] constructors = getConstructorsSortedByArgsCount(targetClass);
        for (Constructor<T> constructor : constructors) {
            T instance = null;
            try {
                instance = getComponentBy(constructor);
                injectComponentBySetter(instance);
            } catch (CycleDependencyException e) {
                throw new CycleDependencyException();
            } catch (Exception e) {
            }

            if (instance != null) {
                classesUnderConstruct.remove(targetClass);
                return instance;
            }
        }

        if (parent != null) {
            return parent.getComponent(clazz);
        }

        throw new AssembleComponentFailedException();
    }

    private <T> T injectComponentBySetter(T instance) throws InvocationTargetException, IllegalAccessException {
        Method[] clazzMethods = instance.getClass().getMethods();
        for (Method method : clazzMethods) {
            if (isSetterMethod(method)) {
                method.invoke(instance, resolveObjects(method.getParameterTypes()));
            }
        }

        return instance;
    }

    private boolean isSetterMethod(Method method) {
        //TODO: refactor to using regex
        return method.getName().startsWith("set");
    }

    private <T> T getComponentBy(Constructor<T> constructor) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Class[] parameterTypes = constructor.getParameterTypes();

        if (parameterTypes.length == 0) {
            return constructor.newInstance();
        }

        return constructor.newInstance(resolveObjects(parameterTypes));
    }

    private Object[] resolveObjects(Class[] parameterTypes) {
        ArrayList params = new ArrayList(parameterTypes.length);
        for (Class paramClazz : parameterTypes) {
            params.add(doGetComponent(paramClazz));
        }

        return params.toArray();
    }

    private <T> Constructor<T>[] getConstructorsSortedByArgsCount(Class<T> clazz) {
        Constructor<T>[] constructors = (Constructor<T>[])clazz.getDeclaredConstructors();
        Arrays.sort(constructors, new Comparator<Constructor<?>>() {
            @Override
            public int compare(Constructor<?> constructorA, Constructor<?> constructorB) {
                return constructorB.getParameterTypes().length - constructorA.getParameterTypes().length;
            }
        });

        return constructors;
    }

    public void addComponent(Class interfaceClazz, Class implClazz) {
        implementationsMap.put(interfaceClazz, implClazz);
    }

    public void addComponent(Class interfaceClazz, Object instance) {
        instancesMap.put(interfaceClazz, instance);
    }
}
