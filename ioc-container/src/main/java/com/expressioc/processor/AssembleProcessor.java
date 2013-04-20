package com.expressioc.processor;

public interface AssembleProcessor {
    public void initBeforeGetComponentFromContainer();

    public <T> Class<? extends T> beforeAssemble(Class<T> clazz);
    public void postAssemble(Class clazz, Object instance);
}
