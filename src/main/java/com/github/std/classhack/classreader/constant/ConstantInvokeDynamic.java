package com.github.std.classhack.classreader.constant;

public class ConstantInvokeDynamic implements Constant{
    private final int bootstrapMethodAttrIndex;
    private final int nameAndTypeIndex;

    public ConstantInvokeDynamic(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
        this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    public int getBootstrapMethodAttrIndex() {
        return bootstrapMethodAttrIndex;
    }

    public int getNameAndTypeIndex() {
        return nameAndTypeIndex;
    }
}
