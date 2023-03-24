package com.github.std.classhack.engine.classloader.constant;

public class ConstantInterfaceMethodRef implements Constant {
    private final int classIndex;
    private final int nameAndTypeIndex;

    public ConstantInterfaceMethodRef(int classIndex, int nameAndTypeIndex) {
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
