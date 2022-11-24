package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.ClassFile;
import com.github.std.mtvm.engine.classloader.constant.Constant;

import java.io.IOException;
import java.io.InputStream;

import static com.github.std.mtvm.engine.classloader.attribute.AttributeChecker.checkConstantValue;
import static com.github.std.mtvm.engine.classloader.attribute.AttributeTable.getAttrLen;
import static com.github.std.mtvm.engine.util.BytesReader.readBytes2;

public final class ConstantValue implements AttributeInfo {
    private final Constant value;

    public ConstantValue(Constant value) {
        this.value = value;
    }

    public static AttributeInfo parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long length = getAttrLen(input);
        if (length != 2) {
            throw new ClassFormatError();
        }
        int constIndex = readBytes2(input);

        Constant value = checkConstantValue(constIndex, metaData.constantPool);
        return new ConstantValue(value);
    }

    public Constant getValue() {
        return value;
    }
}