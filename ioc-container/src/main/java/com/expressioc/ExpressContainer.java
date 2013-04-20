package com.expressioc;

import com.expressioc.exception.AssembleComponentFailedException;
import com.expressioc.exception.CycleDependencyException;
import com.expressioc.provider.Assembler;
import com.expressioc.provider.CacheObjectAssembler;
import com.expressioc.provider.impl.InstanceInjectionAssembler;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.expressioc.provider.Assembler.Type.INSTANCE_ASSEMBLER;

public class ExpressContainer implements Container{
    private Container parent;

    private Multimap<Assembler.Type, Assembler> assemblers = HashMultimap.create();

    private Map<Class, Class> implementationsMap = new HashMap<Class, Class>();
    private Set<Class> classesUnderConstruct = new HashSet<Class>();

    public ExpressContainer() {
        assemblers.put(INSTANCE_ASSEMBLER, new InstanceInjectionAssembler());
    }

    public void setParent(ExpressContainer parent) {
        this.parent = parent;
    }

    @Override
    public <T> T getComponent(Class<T> clazz) {
        this.classesUnderConstruct.clear();
        return doGetComponent(clazz);
    }

    private <T> T doGetComponent(Class<T> clazz) {
        T instance = getFromInstanceAssembler(clazz);
        if (instance != null) {
            return instance;
        }

        Class concreteClass = implementationsMap.get(clazz);
        Class targetClass = concreteClass == null ? clazz : concreteClass;

        if (classesUnderConstruct.contains(targetClass)) {
            throw new CycleDependencyException();
        }

        classesUnderConstruct.add(targetClass);

        Constructor<T>[] constructors = getConstructorsSortedByArgsCount(targetClass);
        for (Constructor<T> constructor : constructors) {
            instance = null;
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

    public void addComponent(Class clazz, Object instance) {
        Collection<Assembler> instanceAssemblers = assemblers.get(INSTANCE_ASSEMBLER);
        for (Assembler assembler : instanceAssemblers) {
            ((CacheObjectAssembler)assembler).feedAssembler(clazz, instance);
        }
    }

    private <T> T getFromInstanceAssembler(Class<T> clazz) {
        Collection<Assembler> instanceAssemblers = assemblers.get(INSTANCE_ASSEMBLER);

        for (Assembler assembler : instanceAssemblers) {
            T instance = ((CacheObjectAssembler)assembler).getInstanceBy(clazz);

            if (instance != null) {
                return instance;
            }
        }

        return null;
    }

}
