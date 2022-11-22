package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.constant.Constant;
import com.github.std.mtvm.engine.classloader.constant.ConstantPool;

import java.io.IOException;
import java.io.InputStream;

import static com.github.std.mtvm.engine.classloader.attribute.AttributeChecker.checkConstantValue;
import static com.github.std.mtvm.engine.classloader.attribute.AttributeTable.getAttrLength;
import static com.github.std.mtvm.engine.util.BytesReader.readBytes2;

public final class ConstantValue implements AttributeInfo {
    private final Constant value;

    public ConstantValue(InputStream input, ConstantPool constantPool) throws IOException {
        long length = getAttrLength(input);
        if (length != 2) {
            throw new ClassFormatError();
        }
        int constIndex = readBytes2(input);

        Constant constant = constantPool.getPool().get(constIndex - 1);
        checkConstantValue(constant);
        this.value = constant;
    }

    public Constant getValue() {
        return value;
    }
}