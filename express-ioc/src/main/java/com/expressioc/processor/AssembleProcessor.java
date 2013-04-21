package com.expressioc.processor;

public interface AssembleProcessor {
    public void initBeforeGetComponentFromContainer();

    public void beforeAssemble(Class clazz);
    public Object postAssemble(Class clazz, Object instance);
}
