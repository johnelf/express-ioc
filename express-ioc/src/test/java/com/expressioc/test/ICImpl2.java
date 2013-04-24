package com.expressioc.test;

import com.expressioc.Container;
import com.expressioc.scope.ContainerAware;

public class ICImpl2 implements IC, ContainerAware {
    private Container whichContainerIAmIn;

    @Override
    public void awareContainer(Container container) {
        this.whichContainerIAmIn = container;
    }

    public Container getWhichContainerIAmIn() {
        return whichContainerIAmIn;
    }
}
