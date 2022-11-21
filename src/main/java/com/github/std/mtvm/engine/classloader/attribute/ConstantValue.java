package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.constant.Constant;
import com.github.std.mtvm.engine.classloader.constant.ConstantPool;

import java.io.IOException;
import java.io.InputStream;

import static com.github.std.mtvm.engine.classloader.attribute.AttributeTable.getAttrLength;
import static com.github.std.mtvm.engine.util.ByteUtil.byteArrayToInt;

public final class ConstantValue implements AttributeInfo {
    private final Constant value;

    public ConstantValue(InputStream input, ConstantPool constantPool, AttributeChecker checker) throws IOException {
        long length = getAttrLength(input);
        if (length != 2) {
            throw new ClassFormatError();
        }

        byte[] bsConstIndex = new byte[2];
        int read = input.read(bsConstIndex);
        assert read == 2;

        int constIndex = byteArrayToInt(bsConstIndex);
        Constant constant = constantPool.getPool().get(constIndex - 1);
        checker.checkConstantValue(constant);
        this.value = constant;
    }

    public Constant getValue() {
        return value;
    }
}