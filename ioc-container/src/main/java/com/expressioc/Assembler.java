package com.expressioc;

import com.expressioc.parameters.Parameter;

public interface Assembler {
    public <T> T getInstanceBy(Class<T> clazz, Parameter... arguments);

    Assembler setContainer(ExpressContainer container);
}
