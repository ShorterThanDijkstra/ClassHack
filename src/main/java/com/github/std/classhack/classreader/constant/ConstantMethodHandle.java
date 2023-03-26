package com.github.std.classhack.classreader.constant;

public class ConstantMethodHandle implements Constant{
    private final int referenceKind;
    private final int referenceIndex;

    public ConstantMethodHandle(int referenceKind, int referenceIndex) {
        this.referenceKind = referenceKind;
        this.referenceIndex = referenceIndex;
    }

    public int getReferenceKind() {
        return referenceKind;
    }

    public int getReferenceIndex() {
        return referenceIndex;
    }
}
