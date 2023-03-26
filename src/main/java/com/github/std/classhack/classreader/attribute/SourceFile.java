package com.github.std.classhack.classreader.attribute;

import com.github.std.classhack.classreader.ClassFile;

import java.io.IOException;
import java.io.InputStream;

import static com.github.std.classhack.classreader.util.BytesReader.readBytes2;

public final class SourceFile implements AttributeInfo {
    private final String sourceFile;

    public SourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public static SourceFile parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long len = AttributeTable.getAttrLen(input);
        if (len != 2) {
            throw new ClassFormatError();
        }
        int sourceFileIndex = readBytes2(input);
        String sourceFile = AttributeChecker.checkSourceFile(sourceFileIndex, metaData.constantPool);
        return new SourceFile(sourceFile);
    }

    public String getSourceFile() {
        return sourceFile;
    }
}