package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.ClassFile;

import java.io.IOException;
import java.io.InputStream;

import static com.github.std.mtvm.engine.classloader.attribute.AttributeTable.getAttrLen;

public final class Synthetic implements AttributeInfo {

    public Synthetic(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long len = getAttrLen(input);
        if (len != 0) {
            throw new ClassFormatError();
        }
    }
}