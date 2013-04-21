package com.expressioc.processor.impl;

import com.expressioc.Container;
import com.expressioc.processor.AssembleProcessor;

import static com.google.common.base.Preconditions.checkNotNull;

public class GetParentComponentProcessor implements AssembleProcessor {
    private final Container parentContainer;

    public GetParentComponentProcessor(Container parentContainer) {
        checkNotNull(parentContainer);
        this.parentContainer = parentContainer;
    }

    @Override
    public void initBeforeGetComponentFromContainer() {
    }

    @Override
    public void beforeAssemble(Class clazz) {
    }

    @Override
    public Object postAssemble(Class clazz, Object assembledObjectFromChild) {
        if (assembledObjectFromChild == null) {
            return parentContainer.getComponent(clazz);
        }

        return assembledObjectFromChild;
    }
}
