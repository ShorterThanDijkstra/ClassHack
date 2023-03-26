package com.github.std.classhack.classreader.constant;

public class ConstantFieldRef implements Constant {
    private final int classIndex;
    private final int nameAndTypeIndex;

    public ConstantFieldRef(int classIndex, int nameAndTypeIndex) {
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
