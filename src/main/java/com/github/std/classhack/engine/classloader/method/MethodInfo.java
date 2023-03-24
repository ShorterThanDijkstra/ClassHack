package com.github.std.classhack.engine.classloader.method;

import com.github.std.classhack.engine.classloader.attribute.AttributeTable;

import java.util.List;

public class MethodInfo {
    private final List<String> accessFlags;
    private final String name;
    private final String descriptor;
    private final AttributeTable attributeTable;

    static class MethodInfoBuilder {
        List<String> accessFlags;
        String name;
        String descriptor;
        AttributeTable attributeTable;

        MethodInfo build() {
            return new MethodInfo(
                    accessFlags,
                    name,
                    descriptor,
                    attributeTable
            );
        }
    }

    public MethodInfo(List<String> accessFlags, String name, String descriptor, AttributeTable attributeTable) {
        this.accessFlags = accessFlags;
        this.name = name;
        this.descriptor = descriptor;
        this.attributeTable = attributeTable;
    }

    public List<String> getAccessFlags() {
        return accessFlags;
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public AttributeTable getAttributeTable() {
        return attributeTable;
    }
}
