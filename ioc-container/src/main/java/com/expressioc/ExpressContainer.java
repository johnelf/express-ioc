package com.expressioc;

import com.expressioc.movie.FooMovieFinder;
import com.expressioc.movie.MovieFinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ExpressContainer implements Container{
    private Container parent;
    private BeanFactory beanCreator;
    private ConfigurationLoader loader;
    private Map<Class, Class> implementationsMap = new HashMap<Class, Class>();
    private Map<Class, Object> instancesMap = new HashMap<Class, Object>();

    public Container getParent() {
        return parent;
    }

    public void setParent(Container parent) {
        this.parent = parent;
    }

    @Override
    public <T> T getComponent(Class<T> clazz) {
        Object targetInstance = instancesMap.get(clazz);
        if (targetInstance != null) {
            return (T) targetInstance;
        }

        Class concreteClass = implementationsMap.get(clazz);
        Class targetClass = concreteClass == null ? clazz : concreteClass;
        Constructor<T>[] constructors = getConstructorsSortedByArgsCount(targetClass);

        for (Constructor<T> constructor : constructors) {
            T instance = null;
            try {
                instance = getComponentBy(constructor);
            } catch (InvocationTargetException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            if (instance != null) {
                return instance;
            }
        }

        throw new NullPointerException();
    }

    private <T> T getComponentBy(Constructor<T> constructor) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Class[] parameterTypes = constructor.getParameterTypes();

        if (parameterTypes.length == 0) {
            return constructor.newInstance();
        }

        ArrayList params = new ArrayList(parameterTypes.length);
        for (Class paramClazz : parameterTypes) {
            params.add(getComponent(paramClazz));
        }

        return constructor.newInstance(params.toArray());
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

    public void addComponent(Class<MovieFinder> interfaceClazz, Class<FooMovieFinder> implClazz) {
        implementationsMap.put(interfaceClazz, implClazz);
    }

    public void addComponent(Class<MovieFinder> interfaceClazz, Object instance) {
        instancesMap.put(interfaceClazz, instance);
    }
}
