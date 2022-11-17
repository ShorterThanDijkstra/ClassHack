package com.github.std.mtjvm.engine.classloader.constant;

public class ConstantClass implements Constant{
    public int getNameIndex() {
        return nameIndex;
    }

    private final int nameIndex;

    public ConstantClass(int nameIndex) {
        this.nameIndex = nameIndex;
    }
}
