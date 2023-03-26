package com.github.std.classhack.classreader.attribute;

import com.github.std.classhack.classreader.ClassFile;

import java.io.IOException;
import java.io.InputStream;

import static com.github.std.classhack.classreader.attribute.AttributeTable.getAttrLen;

public final class SourceDebugExtension implements AttributeInfo {
    private final String debugExtension;

    public SourceDebugExtension(String debugExtension) {
        this.debugExtension = debugExtension;
    }

    public static SourceDebugExtension parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        int len = (int) getAttrLen(input);

        byte[] bytes = new byte[len];
        int read = input.read(bytes);
        assert read == len;

        return new SourceDebugExtension(new String(bytes));
    }

    public String getDebugExtension() {
        return debugExtension;
    }
}