package com.expressioc.test;

public class IA_As_ConstructorArg {
    private final IA interfaceAImpl;

    public IA_As_ConstructorArg(IA interfaceAImpl) {
        this.interfaceAImpl = interfaceAImpl;
    }

    public IA getInterfaceA() {
        return interfaceAImpl;
    }
}
