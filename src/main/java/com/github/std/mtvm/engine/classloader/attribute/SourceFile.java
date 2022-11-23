package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.ClassFile;

import java.io.IOException;
import java.io.InputStream;

import static com.github.std.mtvm.engine.classloader.attribute.AttributeChecker.checkSourceFile;
import static com.github.std.mtvm.engine.classloader.attribute.AttributeTable.getAttrLen;
import static com.github.std.mtvm.engine.util.BytesReader.readBytes2;

public final class SourceFile implements AttributeInfo {
    private final String sourceFile;

    public SourceFile(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long len = getAttrLen(input);
        if (len != 2) {
            throw new ClassFormatError();
        }
        int sourceFileIndex = readBytes2(input);
        sourceFile = checkSourceFile(sourceFileIndex, metaData.constantPool);
    }

    public String getSourceFile() {
        return sourceFile;
    }
}