package com.github.std.classhack.classreader.attribute;

public class Unknown implements AttributeInfo{
    private final String name;

    public Unknown(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
