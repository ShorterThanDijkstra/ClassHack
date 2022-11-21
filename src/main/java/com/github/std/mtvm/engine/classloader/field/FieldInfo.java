package com.github.std.mtvm.engine.classloader.field;

import com.github.std.mtvm.engine.classloader.attribute.AttributeTable;

import java.util.List;

public class FieldInfo {
    private final List<String> accessFlags;
    private final String name;
    private final String descriptor;
    private final AttributeTable attributeTable;

    static class FieldInfoBuilder {
        List<String> accessFlags;
        String name;
        String descriptor;
        AttributeTable attributeTable;

         FieldInfo build() {
            return new FieldInfo(accessFlags, name, descriptor, attributeTable);
        }
    }

    public FieldInfo(List<String> accessFlags,
                     String name,
                     String descriptor,
                     AttributeTable attributeTable) {
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
