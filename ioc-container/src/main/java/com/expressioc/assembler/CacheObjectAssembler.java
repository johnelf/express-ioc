package com.expressioc.assembler;

import static com.expressioc.assembler.Assembler.Type.OBJECT_CACHE_ASSEMBLER;

public abstract class CacheObjectAssembler implements Assembler {
    @Override
    public Type getAssemblerType() {
        return OBJECT_CACHE_ASSEMBLER;
    }
}
