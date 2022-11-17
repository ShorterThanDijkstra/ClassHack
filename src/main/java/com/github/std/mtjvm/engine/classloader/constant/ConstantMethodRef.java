package com.github.std.mtjvm.engine.classloader.constant;

public class ConstantMethodRef implements Constant {
    private final int classIndex;
    private final int nameAndTypeIndex;

    public ConstantMethodRef(int classIndex, int nameAndTypeIndex) {
        this.classIndex = classIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    public int getClassIndex() {
        return classIndex;
    }

    public int getNameAndTypeIndex() {
        return nameAndTypeIndex;
    }
}
