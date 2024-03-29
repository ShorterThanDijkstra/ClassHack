package com.github.std.classhack.classreader.attribute;

import com.github.std.classhack.classreader.ClassFile;

import java.io.IOException;
import java.io.InputStream;

import static com.github.std.classhack.classreader.attribute.AttributeChecker.checkSignature;
import static com.github.std.classhack.classreader.attribute.AttributeTable.getAttrLen;
import static com.github.std.classhack.classreader.util.BytesReader.readBytes2;

public final class Signature implements AttributeInfo {
    private final String signature;

    public String getSignature() {
        return signature;
    }

    public Signature(String signature) {
        this.signature = signature;
    }

    public static Signature parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long len = getAttrLen(input);
        if (len != 2) {
            throw new ClassFormatError();
        }
        int signIndex = readBytes2(input);
        String signature = checkSignature(signIndex, metaData.constantPool);
        return new Signature(signature);
    }
}