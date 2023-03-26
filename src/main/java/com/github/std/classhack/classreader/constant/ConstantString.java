package com.github.std.classhack.classreader.constant;

public class ConstantString implements Constant{
    private final int stringIndex;

    public ConstantString(int stringIndex) {
        this.stringIndex = stringIndex;
    }

    public int getStringIndex() {
        return stringIndex;
    }
}
