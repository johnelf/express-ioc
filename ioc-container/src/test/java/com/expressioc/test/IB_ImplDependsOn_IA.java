package com.expressioc.test;

public class IB_ImplDependsOn_IA implements IB {
    private IA iaImpl;

    public IB_ImplDependsOn_IA(IA iaImpl) {
        this.iaImpl = iaImpl;
    }
}
