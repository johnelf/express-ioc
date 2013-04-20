package com.expressioc;

public interface Assembler {
    public <T> T getInstanceBy(Class<T> clazz);
}
