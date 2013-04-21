package com.expressioc.test;

public class IA_ImplDependsOn_IB implements IA{
    private IB directorFinder;

    public IA_ImplDependsOn_IB(IB ibImpl) {
        this.directorFinder = ibImpl;
    }

}
