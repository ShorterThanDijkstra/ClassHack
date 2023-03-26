package com.github.std.classhack.classreader.constant;

public class ConstantFloat implements Constant {
    private final float value;

    public ConstantFloat(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}
