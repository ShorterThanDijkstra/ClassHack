package com.github.std.mtvm.engine.classloader.attribute;

public class Unknown implements AttributeInfo{
    private final String name;

    public Unknown(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
