package com.expressioc;

import com.expressioc.exception.AssembleComponentFailedException;
import com.expressioc.processor.AssembleProcessor;
import com.expressioc.processor.impl.CycleDependencyDetectProcessor;
import com.expressioc.processor.impl.GetParentComponentProcessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class ExpressContainer implements Container{
    private Map<Class, Object> instancesMap = new HashMap<Class, Object>();
    private Map<Class, Class> implementationsMap = new HashMap<Class, Class>();
    private List<AssembleProcessor> assembleProcessors = new ArrayList<AssembleProcessor>();
    private Assembler assembler;

    public ExpressContainer() {
        this(null);
    }

    public ExpressContainer(Container parentContainer) {
        assembler = new ConstructorAssembler(this);
        assembleProcessors.add(new CycleDependencyDetectProcessor());

        if (parentContainer != null) {
            assembleProcessors.add(new GetParentComponentProcessor(parentContainer));
        }
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

    <T> T doGetComponent(Class<T> clazz) {
        T instance = getInstanceToInjectIfHave(clazz);
        if (instance != null) {
            return instance;
        }

        clazz = getImplementationClassIfHave(clazz);

        invokePreProcessor(clazz);
        instance = assembler.getInstanceBy(clazz);

        try {
            if (instance != null)
                injectComponentBySetter(instance);
        } catch (InvocationTargetException e) {
        } catch (IllegalAccessException e) {
        }

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

    private Object[] resolveObjects(Class[] parameterTypes) {
        ArrayList params = new ArrayList(parameterTypes.length);
        for (Class paramClazz : parameterTypes) {
            params.add(doGetComponent(paramClazz));
        }

        return params.toArray();
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
