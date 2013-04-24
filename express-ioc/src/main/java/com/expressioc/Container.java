package com.expressioc;

import java.util.List;

public interface Container {
    public <T> T getComponent(Class<T> clazz);

    public <T> List<T> getImplementationObjectListOf(Class<T> clazz);
}