package com.expressioc.test;

public class IBImplWithConstantConstructorArgs implements IB{
    private final String name;
    private IA iaImplInstance;
    private Integer age;
    private Long distance;

    public IBImplWithConstantConstructorArgs(String name, Integer age, Long distance) {
        this.name = name;
        this.age = age;
        this.distance = distance;
    }

    public IBImplWithConstantConstructorArgs(String name, IA iAImplInstance) {
        this.name = name;
        this.iaImplInstance = iAImplInstance;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public Long getDistance() {
        return distance;
    }

    public IA getIAImplInstance() {
        return iaImplInstance;
    }
}
