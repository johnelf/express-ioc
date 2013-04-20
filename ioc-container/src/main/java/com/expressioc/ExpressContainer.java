package com.expressioc;

import com.expressioc.exception.AssembleComponentFailedException;
import com.expressioc.processor.AssembleProcessor;
import com.expressioc.processor.impl.CycleDependencyDetectProcessor;
import com.expressioc.processor.impl.GetParentComponentProcessor;

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
    private DependencySetter dependencySetter;

    public ExpressContainer() {
        this(null, new ConstructorAssembler(), new DefaultDependencySetter());
    }

    public ExpressContainer(Container parentContainer) {
        this(parentContainer, new ConstructorAssembler(), new DefaultDependencySetter());
    }

    public ExpressContainer(Container parentContainer, Assembler assembler, DependencySetter dependencySetter) {
        this.assembler = assembler.setContainer(this);
        this.dependencySetter = dependencySetter.setContainer(this);

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

        if (instance != null) {
            dependencySetter.setDependencies(instance);
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
