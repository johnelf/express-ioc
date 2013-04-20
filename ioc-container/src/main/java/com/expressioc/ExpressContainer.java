package com.expressioc;

import com.expressioc.exception.AssembleComponentFailedException;
import com.expressioc.exception.CycleDependencyException;
import com.expressioc.processor.AssembleProcessor;
import com.expressioc.processor.impl.CycleDependencyDetectProcessor;
import com.expressioc.processor.impl.GetParentComponentProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

public class ExpressContainer implements Container{
    private Container parent;

    private Map<Class, Object> instancesMap = new HashMap<Class, Object>();
    private Map<Class, Class> implementationsMap = new HashMap<Class, Class>();
    private List<AssembleProcessor> assembleProcessors = new ArrayList<AssembleProcessor>();

    public ExpressContainer() {
        assembleProcessors.add(new CycleDependencyDetectProcessor());
    }

    public ExpressContainer(Container parentContainer) {
        this.parent = parentContainer;

        assembleProcessors.add(new CycleDependencyDetectProcessor());
        assembleProcessors.add(new GetParentComponentProcessor(parentContainer));
    }

    @Override
    public <T> T getComponent(Class<T> clazz) {
        initProcessors();
        return doGetComponent(clazz);
    }

    private void initProcessors() {
        for (AssembleProcessor processor : assembleProcessors) {
            processor.initBeforeGetComponentFromContainer();
        }
    }

    private <T> T doGetComponent(Class<T> clazz) {
        T instance = getInstanceToInjectIfHave(clazz);
        if (instance != null) {
            return instance;
        }

        clazz = getImplementationClassIfHave(clazz);

        invokePreProcessor(clazz);
        instance = getInstanceFromConstructor(clazz);
        instance = invokePostProcessors(clazz, instance);

        if (instance != null) {
            return instance;
        }

        throw new AssembleComponentFailedException();
    }

    private Class getImplementationClassIfHave(Class clazz) {
        return implementationsMap.containsKey(clazz) ? implementationsMap.get(clazz) : clazz;
    }

    private <T> T invokePostProcessors(Class<T> clazz, T instance) {
        for (AssembleProcessor processor : assembleProcessors) {
            instance = (T) processor.postAssemble(clazz, instance);
        }

        return instance;
    }

    private <T> void invokePreProcessor(Class<T> clazz) {
        for (AssembleProcessor processor : assembleProcessors) {
            processor.beforeAssemble(clazz);
        }
    }

    private <T> T getInstanceFromConstructor(Class targetClass) {
        Constructor<T>[] constructors = getConstructorsSortedByArgsCount(targetClass);
        for (Constructor<T> constructor : constructors) {
            T constructingInstance = null;
            try {
                constructingInstance = getComponentBy(constructor);
                injectComponentBySetter(constructingInstance);
            } catch (CycleDependencyException e) {
                throw new CycleDependencyException();
            } catch (Exception e) {
            }

            if (constructingInstance != null) {
                return constructingInstance;
            }
        }

        return null;
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

    public void addComponent(Class clazz, Object instance) {
        checkArgument(clazz.isInstance(instance), "Expect: clazz.isInstance(instance)", clazz, instance);
        instancesMap.put(clazz, instance);
    }

    private <T> T getInstanceToInjectIfHave(Class<T> clazz) {
        return (T)instancesMap.get(clazz);
    }

}
