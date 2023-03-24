package com.github.std.classhack.engine.classloader.attribute;

import com.github.std.classhack.engine.classloader.ClassFile;

import java.io.IOException;
import java.io.InputStream;

import static com.github.std.classhack.engine.classloader.attribute.AttributeChecker.checkEnclosingMethodClassIndex;
import static com.github.std.classhack.engine.classloader.attribute.AttributeChecker.checkEnclosingMethodMethodIndex;
import static com.github.std.classhack.engine.classloader.attribute.AttributeTable.getAttrLen;
import static com.github.std.classhack.engine.util.BytesReader.readBytes2;

public final class EnclosingMethod implements AttributeInfo {
    private final String outerClass;
    private final String outerMethod;

    private EnclosingMethod(String outerClass, String outerMethod) {
        this.outerClass = outerClass;
        this.outerMethod = outerMethod;
    }

    public String getOuterClass() {
        return outerClass;
    }

    public String getOuterMethod() {
        return outerMethod;
    }

    public static EnclosingMethod parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long len = getAttrLen(input);
        assert len == 4;

        int classIndex = readBytes2(input);
        String outerClass = checkEnclosingMethodClassIndex(classIndex, metaData.constantPool);

        int methodIndex = readBytes2(input);
        String outerMethod = checkEnclosingMethodMethodIndex(methodIndex, metaData.constantPool);
        return new EnclosingMethod(outerClass, outerMethod);
    }
}