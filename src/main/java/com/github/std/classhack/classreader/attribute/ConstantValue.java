package com.github.std.classhack.classreader.attribute;

import com.github.std.classhack.classreader.ClassFile;
import com.github.std.classhack.classreader.constant.Constant;

import java.io.IOException;
import java.io.InputStream;

import static com.github.std.classhack.classreader.util.BytesReader.readBytes2;

public final class ConstantValue implements AttributeInfo {
    private final Constant value;

    public ConstantValue(Constant value) {
        this.value = value;
    }

    public static AttributeInfo parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long length = AttributeTable.getAttrLen(input);
        if (length != 2) {
            throw new ClassFormatError();
        }
        int constIndex = readBytes2(input);

        Constant value = AttributeChecker.checkConstantValue(constIndex, metaData.constantPool);
        return new ConstantValue(value);
    }

    public Constant getValue() {
        return value;
    }
}