package com.expressioc.provider;

public interface Assembler {
    public enum Type {
        OBJECT_CACHE_ASSEMBLER,
        INSTANCE_ASSEMBLER
    }

    public Type getAssemblerType();
    public <T> T getInstanceBy(Class<T> clazz);
}
