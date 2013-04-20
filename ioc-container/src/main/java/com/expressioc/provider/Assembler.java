package com.expressioc.provider;

public interface Assembler {
    public enum Type {
        INSTANCE_ASSEMBLER
    }

    public Type getAssemblerType();
}
