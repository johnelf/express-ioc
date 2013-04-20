package com.expressioc.assembler;

import static com.expressioc.assembler.Assembler.Type.INSTANCE_ASSEMBLER;

public abstract class InstanceAssembler implements Assembler{
    public Type getAssemblerType() {
        return INSTANCE_ASSEMBLER;
    }

}
