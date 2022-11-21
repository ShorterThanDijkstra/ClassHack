package com.github.std.mtvm.engine.classloader.constant;

public class ConstantLong implements Constant {
    private final long value;

    public ConstantLong(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
