package com.github.std.classhack.engine.classloader.attribute;

import com.github.std.classhack.engine.classloader.ClassFile;

import java.io.IOException;
import java.io.InputStream;

import static com.github.std.classhack.engine.classloader.attribute.AttributeChecker.checkSourceFile;
import static com.github.std.classhack.engine.classloader.attribute.AttributeTable.getAttrLen;
import static com.github.std.classhack.engine.util.BytesReader.readBytes2;

public final class SourceFile implements AttributeInfo {
    private final String sourceFile;

    public SourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public static SourceFile parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long len = getAttrLen(input);
        if (len != 2) {
            throw new ClassFormatError();
        }
        int sourceFileIndex = readBytes2(input);
        String sourceFile = checkSourceFile(sourceFileIndex, metaData.constantPool);
        return new SourceFile(sourceFile);
    }

    public String getSourceFile() {
        return sourceFile;
    }
}