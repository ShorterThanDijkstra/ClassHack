package com.github.std.classhack.classreader.attribute;

import com.github.std.classhack.classreader.ClassFile;

import java.io.IOException;
import java.io.InputStream;

public final class AnnotationDefault implements AttributeInfo {
    private final ElementValue defaultValue;

    public AnnotationDefault(ElementValue defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ElementValue getDefaultValue() {
        return defaultValue;
    }

    public static AnnotationDefault parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long len = AttributeTable.getAttrLen(input);
        ElementValue elementValue = ElementValue.parse(input, metaData);
        return new AnnotationDefault(elementValue);
    }
}