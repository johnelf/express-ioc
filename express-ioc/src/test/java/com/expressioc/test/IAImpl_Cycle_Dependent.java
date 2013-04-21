package com.expressioc.test;

public class IAImpl_Cycle_Dependent implements IA {
    private IA iaImpl;

    public IAImpl_Cycle_Dependent(IA iaImpl) {
        this.iaImpl = iaImpl;
    }

}
