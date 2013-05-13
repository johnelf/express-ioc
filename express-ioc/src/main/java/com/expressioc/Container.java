package com.expressioc;

import com.expressioc.parameters.Parameter;

import java.util.List;

public interface Container {
    public <T> T getComponent(Class<T> clazz);

    public <T> List<T> getImplementationObjectListOf(Class<T> clazz);

    void addComponent(Class interfaceClazz, Class implClazz, Parameter... constructorParams);

    void addComponent(Class clazz, Object instance);

    void addComponent(Class interfaceClazz, Class implClazz);
}