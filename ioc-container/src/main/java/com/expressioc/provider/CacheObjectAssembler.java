package com.expressioc.provider;

import static com.expressioc.provider.Assembler.Type.OBJECT_CACHE_ASSEMBLER;

public abstract class CacheObjectAssembler implements Assembler {
    @Override
    public Type getAssemblerType() {
        return OBJECT_CACHE_ASSEMBLER;
    }

    public abstract boolean feedAssembler(Class clazz, Object instance);
}
