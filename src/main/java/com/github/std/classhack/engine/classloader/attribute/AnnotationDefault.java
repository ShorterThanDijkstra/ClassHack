package com.github.std.classhack.engine.classloader.attribute;

import com.github.std.classhack.engine.classloader.ClassFile;

import java.io.IOException;
import java.io.InputStream;

import static com.github.std.classhack.engine.classloader.attribute.AttributeTable.getAttrLen;

public final class AnnotationDefault implements AttributeInfo {
    private final ElementValue defaultValue;

    public AnnotationDefault(ElementValue defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ElementValue getDefaultValue() {
        return defaultValue;
    }

    public static AnnotationDefault parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long len = getAttrLen(input);
        ElementValue elementValue = ElementValue.parse(input, metaData);
        return new AnnotationDefault(elementValue);
    }
}