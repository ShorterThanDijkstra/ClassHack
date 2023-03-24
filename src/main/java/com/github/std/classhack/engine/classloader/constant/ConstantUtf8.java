package com.github.std.classhack.engine.classloader.constant;

public class ConstantUtf8 implements Constant {
    private final String value;

    public ConstantUtf8(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
