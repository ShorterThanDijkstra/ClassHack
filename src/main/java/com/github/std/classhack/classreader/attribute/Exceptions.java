package com.github.std.classhack.classreader.attribute;

import com.github.std.classhack.classreader.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.classhack.classreader.util.BytesReader.readBytes2;

public final class Exceptions implements AttributeInfo {
    private final List<String> exceptions;

    private Exceptions(List<String> exceptions) {
        this.exceptions = exceptions;
    }

    public static Exceptions parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        AttributeTable.getAttrLen(input);
        int exNum = readBytes2(input);
        List<String> exceptions = new ArrayList<>(exNum);
        for (int i = 0; i < exNum; i++) {
            int exClassIndex = readBytes2(input);
            exceptions.add(
                    AttributeChecker.checkExceptions(exClassIndex, metaData.constantPool)
            );
        }
        return new Exceptions(exceptions);
    }

    public List<String> getExceptions() {
        return exceptions;
    }
}