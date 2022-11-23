package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.ClassFile;

import java.io.IOException;
import java.io.InputStream;

import static com.github.std.mtvm.engine.classloader.attribute.AttributeChecker.checkEnclosingMethodClassIndex;
import static com.github.std.mtvm.engine.classloader.attribute.AttributeChecker.checkEnclosingMethodMethodIndex;
import static com.github.std.mtvm.engine.classloader.attribute.AttributeTable.getAttrLen;
import static com.github.std.mtvm.engine.util.BytesReader.readBytes2;

public final class EnclosingMethod implements AttributeInfo {
    private final String outerClass;
    private final String outerMethod;

    public String getOuterClass() {
        return outerClass;
    }

    public String getOuterMethod() {
        return outerMethod;
    }

    public EnclosingMethod(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long len = getAttrLen(input);
        assert len == 4;

        int classIndex = readBytes2(input);
        outerClass = checkEnclosingMethodClassIndex(classIndex, metaData.constantPool);

        int methodIndex = readBytes2(input);
        outerMethod = checkEnclosingMethodMethodIndex(methodIndex, metaData.constantPool);
    }
}