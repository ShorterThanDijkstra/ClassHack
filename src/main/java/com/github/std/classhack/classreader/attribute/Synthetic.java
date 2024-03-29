package com.github.std.classhack.classreader.attribute;

import com.github.std.classhack.classreader.ClassFile;

import java.io.IOException;
import java.io.InputStream;

import static com.github.std.classhack.classreader.attribute.AttributeTable.getAttrLen;

public final class Synthetic implements AttributeInfo {

    public static Synthetic parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long len = getAttrLen(input);
        if (len != 0) {
            throw new ClassFormatError();
        }
        return new Synthetic();
    }
}