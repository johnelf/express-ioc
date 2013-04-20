package com.expressioc;

import com.expressioc.exception.AssembleComponentFailedException;
import com.expressioc.exception.CycleDependencyException;
import com.expressioc.assembler.Assembler;
import com.expressioc.assembler.impl.InstanceInjectionAssembler;
import com.expressioc.processor.AssembleProcessor;
import com.expressioc.processor.impl.CycleDependencyDetectProcessor;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.expressioc.assembler.Assembler.Type.OBJECT_CACHE_ASSEMBLER;

public class ExpressContainer implements Container{
    private Container parent;

    private Multimap<Assembler.Type, Assembler> assemblers = HashMultimap.create();
    private List<AssembleProcessor> assembleProcessors = new ArrayList<AssembleProcessor>();

    private Map<Class, Class> implementationsMap = new HashMap<Class, Class>();
    private Set<Class> classesUnderConstruct = new HashSet<Class>();

    public ExpressContainer() {
        assemblers.put(OBJECT_CACHE_ASSEMBLER, new InstanceInjectionAssembler());
        assembleProcessors.add(new CycleDependencyDetectProcessor());
    }

    public void setParent(ExpressContainer parent) {
        this.parent = parent;
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
        T instance = getFromObjectCacheAssembler(clazz);
        if (instance != null) {
            return instance;
        }

        Class concreteClass = implementationsMap.get(clazz);
        Class targetClass = concreteClass == null ? clazz : concreteClass;

        for (AssembleProcessor processor : assembleProcessors) {
            processor.beforeAssemble(targetClass);
        }

        instance = getInstanceFromConstructor(targetClass);

        for (AssembleProcessor processor : assembleProcessors) {
            processor.postAssemble(targetClass, instance);
        }

        if (instance != null) {
            return instance;
        }

        if (parent != null) {
            return parent.getComponent(clazz);
        }

        throw new AssembleComponentFailedException();
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
        Collection<Assembler> instanceAssemblers = assemblers.get(OBJECT_CACHE_ASSEMBLER);
        for (Assembler assembler : instanceAssemblers) {
            assembler.feedAssembler(clazz, instance);
        }
    }

    private <T> T getFromObjectCacheAssembler(Class<T> clazz) {
        Collection<Assembler> instanceAssemblers = assemblers.get(OBJECT_CACHE_ASSEMBLER);
        for (Assembler assembler : instanceAssemblers) {
            T instance = assembler.getInstanceBy(clazz);
            if (instance != null) {
                return instance;
            }
        }
        return null;
    }

}
