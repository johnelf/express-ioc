package com.expressioc.test;

public class IC_As_ConstructorArg {
    private IC icInstance;

    public IC_As_ConstructorArg(IC icInstance) {
        this.icInstance = icInstance;
    }

    public IC getIcInstance() {
        return icInstance;
    }
}
