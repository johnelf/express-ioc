package com.expressioc;

public interface DependencySetter {
    public void setDependencies(Object instance);

    DependencySetter setContainer(ExpressContainer container);
}
