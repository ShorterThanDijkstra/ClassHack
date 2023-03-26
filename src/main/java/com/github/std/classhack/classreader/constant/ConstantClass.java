package com.github.std.classhack.classreader.constant;

public class ConstantClass implements Constant {
    public int getNameIndex() {
        return nameIndex;
    }

    private final int nameIndex;

    public ConstantClass(int nameIndex) {
        this.nameIndex = nameIndex;
    }
}
