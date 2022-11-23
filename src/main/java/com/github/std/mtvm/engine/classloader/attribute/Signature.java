package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.ClassFile;

import java.io.IOException;
import java.io.InputStream;

import static com.github.std.mtvm.engine.classloader.attribute.AttributeChecker.checkSignature;
import static com.github.std.mtvm.engine.classloader.attribute.AttributeTable.getAttrLen;
import static com.github.std.mtvm.engine.util.BytesReader.readBytes2;

public final class Signature implements AttributeInfo {
    private final String signature;

    public Signature(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long len = getAttrLen(input);
        if (len != 2) {
            throw new ClassFormatError();
        }
        int signIndex = readBytes2(input);
        signature = checkSignature(signIndex, metaData.constantPool);
    }
}