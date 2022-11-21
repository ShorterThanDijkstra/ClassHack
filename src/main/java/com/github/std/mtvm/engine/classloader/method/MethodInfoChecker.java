package com.github.std.mtvm.engine.classloader.method;

import com.github.std.mtvm.engine.classloader.ClassFile;
import com.github.std.mtvm.engine.classloader.constant.Constant;
import com.github.std.mtvm.engine.classloader.constant.ConstantPool;
import com.github.std.mtvm.engine.classloader.constant.ConstantUtf8;

import java.util.List;

public class MethodInfoChecker {
    public void checkAccessFlags(byte[] accessFlags, ClassFile.ClassFileBuilder metaData) {
        // TODO: 2022/11/21


    }

    public String checkNameIndex(int nameIndex, ConstantPool constantPool) {
        List<Constant> constants = constantPool.getPool();
        Constant constUtf8 = constants.get(nameIndex - 1);
        if (!(constUtf8 instanceof ConstantUtf8)) {
            throw new ClassFormatError();
        }
        return ((ConstantUtf8) constUtf8).getValue();
    }

    public String checkDescIndex(int descIndex, ConstantPool constantPool) {
        List<Constant> constants = constantPool.getPool();
        Constant constUtf8 = constants.get(descIndex - 1);
        if (!(constUtf8 instanceof ConstantUtf8)) {
            throw new ClassFormatError();
        }
        return ((ConstantUtf8) constUtf8).getValue();

    }
}
