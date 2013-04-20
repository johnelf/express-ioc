package com.expressioc.assembler;

public interface Assembler {
    public <T> T getInstanceBy(Class<T> clazz);
}
