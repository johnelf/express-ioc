package com.expressioc.provider;

import static com.expressioc.provider.Assembler.Type.INSTANCE_ASSEMBLER;

public abstract class InstanceAssembler implements Assembler{
    public Type getAssemblerType() {
        return INSTANCE_ASSEMBLER;
    }

    public abstract boolean feedAssembler(Class clazz, Object instance);
    public abstract <T> T getInstanceBy(Class<T> clazz);
}
