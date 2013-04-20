package com.expressioc.assembler;

public interface Assembler {
    public enum Type {
        OBJECT_CACHE_ASSEMBLER,
        INSTANCE_ASSEMBLER
    }

    public Type getAssemblerType();
    public boolean feedAssembler(Class clazz, Object instance);
    public <T> T getInstanceBy(Class<T> clazz);
}
