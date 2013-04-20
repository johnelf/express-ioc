package com.expressioc.parameters;

public class ConstParameter implements Parameter{
    private String value;

    public ConstParameter(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
