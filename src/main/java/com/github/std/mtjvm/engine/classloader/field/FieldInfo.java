package com.github.std.mtjvm.engine.classloader.field;

import com.github.std.mtjvm.engine.classloader.attribute.AttributeTable;

import java.util.List;

public class FieldInfo {
    private final List<String> accessFlags;
    private final String name;
    private final String descriptor;
    private final AttributeTable attributeTable;

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
