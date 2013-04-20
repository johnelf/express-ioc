package com.expressioc.processor;

public interface AssembleProcessor {
    public void initBeforeGetComponentFromContainer();

    public void beforeAssemble(Class clazz);
    public void postAssemble(Class clazz, Object instance);
}
