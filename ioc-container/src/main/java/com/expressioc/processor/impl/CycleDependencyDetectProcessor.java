package com.expressioc.processor.impl;

import com.expressioc.exception.CycleDependencyException;
import com.expressioc.processor.AssembleProcessor;

import java.util.HashSet;
import java.util.Set;

public class CycleDependencyDetectProcessor implements AssembleProcessor {
    private Set<Class> classesUnderConstruct = new HashSet<Class>();

    @Override
    public void initBeforeGetComponentFromContainer() {
        classesUnderConstruct.clear();
    }

    @Override
    public void beforeAssemble(Class clazz) {
        if (classesUnderConstruct.contains(clazz)) {
            throw new CycleDependencyException();
        }

        classesUnderConstruct.add(clazz);
    }

    @Override
    public Object postAssemble(Class clazz, Object instance) {
        if (instance != null) {
            classesUnderConstruct.remove(clazz);
        }

        return instance;
    }
}
