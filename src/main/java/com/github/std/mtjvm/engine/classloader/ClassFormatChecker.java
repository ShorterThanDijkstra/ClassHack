package com.github.std.mtjvm.engine.classloader;

import com.github.std.mtjvm.engine.classloader.constant.Constant;
import com.github.std.mtjvm.engine.classloader.constant.ConstantClass;
import com.github.std.mtjvm.engine.classloader.constant.ConstantPool;
import com.github.std.mtjvm.engine.classloader.constant.ConstantUtf8;

import java.util.List;

public class ClassFormatChecker {
    public void validateMagicNum(byte[] magicNum) {
        if ((magicNum[0] & 0XFF) != 0XCA ||
                (magicNum[1] & 0XFF) != 0XFE ||
                (magicNum[2] & 0XFF) != 0XBA ||
                (magicNum[3] & 0XFF) != 0xBE
        ) {
            throw new ClassFormatError("Wrong Magic Number");
        }
    }

    public String validateThisClass(ConstantPool constantPool, int thisClassIndex) {
        List<Constant> constants = constantPool.getPool();
        Constant constClass = constants.get(thisClassIndex - 1);
        if (!(constClass instanceof ConstantClass)) {
            throw new ClassFormatError();
        }
        int nameIndex = ((ConstantClass) constClass).getNameIndex();
        Constant constUtf8 = constants.get(nameIndex - 1);
        if (!(constUtf8 instanceof ConstantUtf8)) {
            throw new ClassFormatError();
        }
        return ((ConstantUtf8) constUtf8).getValue();
    }

    public String validateSuperClass(ConstantPool constantPool, int superClassIndex, boolean isInterface) {
        if (superClassIndex == 0) {
            return "";
        }
        List<Constant> constants = constantPool.getPool();
        Constant constClass = constants.get(superClassIndex - 1);
        if (!(constClass instanceof ConstantClass)) {
            throw new ClassFormatError();
        }
        int nameIndex = ((ConstantClass) constClass).getNameIndex();
        Constant constUtf8 = constants.get(nameIndex - 1);
        if (!(constUtf8 instanceof ConstantUtf8)) {
            throw new ClassFormatError();
        }

        String superClass = ((ConstantUtf8) constUtf8).getValue();
        if (isInterface && !superClass.equals("java/lang/Object")) {
            throw new ClassFormatError();
        }
        return superClass;
    }

    public String[] validateInterfaces(ConstantPool constantPool, int[] constClassIndices) {
        String[] interfaces = new String[constClassIndices.length];
        List<Constant> constants = constantPool.getPool();
        for (int i = 0; i < constClassIndices.length; i++) {
            int constClassIndex = constClassIndices[i];
            Constant constClass = constants.get(constClassIndex - 1);
            if (!(constClass instanceof ConstantClass)) {
                throw new ClassFormatError();
            }
            int nameIndex = ((ConstantClass) constClass).getNameIndex();
            Constant constUtf8 = constants.get(nameIndex - 1);
            if (!(constUtf8 instanceof ConstantUtf8)) {
                throw new ClassFormatError();
            }
            interfaces[i] = ((ConstantUtf8) constUtf8).getValue();
        }
        return interfaces;
    }
}
