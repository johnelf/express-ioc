package com.expressioc.processor.impl;

import com.expressioc.Container;
import com.expressioc.processor.AssembleProcessor;
import com.expressioc.scope.ContainerAware;

public class ContainerAwareProcessor implements AssembleProcessor {
    private final Container currentContainer;

    public ContainerAwareProcessor(Container currentContainer) {
        this.currentContainer = currentContainer;
    }

    @Override
    public void initBeforeGetComponentFromContainer() {
    }

    @Override
    public void beforeAssemble(Class clazz) {
    }

    @Override
    public Object postAssemble(Class clazz, Object instance) {
        boolean isContainerAwareInstance = instance != null && instance instanceof ContainerAware;
        if (isContainerAwareInstance) {
            ((ContainerAware)instance).awareContainer(currentContainer);
        }

        return instance;
    }
}
