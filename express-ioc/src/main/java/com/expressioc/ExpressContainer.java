package com.expressioc;

import com.expressioc.exception.AssembleComponentFailedException;
import com.expressioc.parameters.Parameter;
import com.expressioc.processor.AssembleProcessor;
import com.expressioc.processor.impl.CycleDependencyDetectProcessor;
import com.expressioc.processor.impl.GetParentComponentProcessor;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class ExpressContainer implements Container{
    private Map<Class, Object> instancesMap = new HashMap<Class, Object>();
    private Map<Class, Class> implementationsMap = new HashMap<Class, Class>();
    private Map<Class, Object> constructorArgs = new HashMap<Class, Object>();
    private List<AssembleProcessor> assembleProcessors = new ArrayList<AssembleProcessor>();
    private Assembler assembler;
    private DependencySetter dependencySetter;
    private String packageToAutoRevealSingleImplementation;

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

    public ExpressContainer(String packageToAutoRevealSingleImplementation) {
        this();
        this.packageToAutoRevealSingleImplementation = packageToAutoRevealSingleImplementation;
    }

    private Class autoFindSingleImplementationOfInterface(Class interfaceClass) {
        ClassPath classpath = null;

        try {
            classpath = ClassPath.from(ClassLoader.getSystemClassLoader());
        } catch (IOException e) {
        }

        Class implementationClasses = null;

        for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClassesRecursive(packageToAutoRevealSingleImplementation)) {
            Class<?> clazz = classInfo.load();

            boolean isImplementationClass = interfaceClass != clazz && interfaceClass.isAssignableFrom(clazz);
            if (isImplementationClass) {
                boolean haveMoreThanOneImplementationClass = implementationClasses != null;
                if (haveMoreThanOneImplementationClass) {
                    return null;
                }

                implementationClasses = clazz;
            }
        }

        return implementationClasses;
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
        {
            instance = assembler.getInstanceBy(clazz, (Parameter[])constructorArgs.get(clazz));
            if (instance != null) {
                dependencySetter.setDependencies(instance);
            }
        }
        instance = invokePostProcessors(clazz, instance);

        if (instance == null) {
            throw new AssembleComponentFailedException();
        }
        return instance;
    }

    private Class getImplementationClassIfHave(Class clazz) {
        Class implClass = implementationsMap.get(clazz);

        if (implClass == null && packageToAutoRevealSingleImplementation != null){
             implClass = autoFindSingleImplementationOfInterface(clazz);
        }

        return implClass == null ? clazz : implClass;
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

    public void addComponent(Class clazz, Object instance) {
        checkArgument(clazz.isInstance(instance), "Expect: clazz.isInstance(instance)", clazz, instance);
        instancesMap.put(clazz, instance);
    }

    public void addComponent(Class interfaceClazz, Class implClazz) {
        implementationsMap.put(interfaceClazz, implClazz);
    }

    public void addComponent(Class interfaceClazz, Class implClazz, Parameter... constructorParams) {
        implementationsMap.put(interfaceClazz, implClazz);

        if (constructorParams.length > 0) {
            constructorArgs.put(implClazz, constructorParams);
        }
    }

    private <T> T getInstanceToInjectIfHave(Class<T> clazz) {
        return (T)instancesMap.get(clazz);
    }
}
