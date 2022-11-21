package com.github.std.mtvm.engine.classloader.constant;

public class ConstantMethodType implements Constant {
    private final int descriptorIndex;

    public ConstantMethodType(int descriptorIndex) {
        this.descriptorIndex = descriptorIndex;
    }

    public int getDescriptorIndex() {
        return descriptorIndex;
    }
}
