package com.github.std.mtjvm.engine.classloader.field;

import com.github.std.mtjvm.engine.classloader.constant.Constant;
import com.github.std.mtjvm.engine.classloader.constant.ConstantPool;
import com.github.std.mtjvm.engine.classloader.constant.ConstantUtf8;

import java.util.List;

public class FieldInfoChecker {
    public void checkAccessFlags(byte[] accessFlags, boolean isInterface) {
        // may have at most one of its ACC_PUBLIC, ACC_PRIVATE, and
        // ACC_PROTECTED flags set
        int pubPriProt = (accessFlags[1] & 0x01) |
                (accessFlags[1] & 0x02) |
                (accessFlags[1] & 0x04);
        if (!(pubPriProt == 1 || pubPriProt == 2 || pubPriProt == 4)) {
            throw new ClassFormatError();
        }

        // must not have both its ACC_FINAL
        // and ACC_VOLATILE flags set
        int finVol = (accessFlags[1] & 0x10) |
                (accessFlags[1] & 0x40);
        if (finVol == (0x10 | 0x40)) {
            throw new ClassFormatError();
        }


        if (isInterface) {
            // Fields of interfaces must have their ACC_PUBLIC, ACC_STATIC, and ACC_FINAL
            // flags set
            int pubStaFin = (accessFlags[1] & 0x01) |
                    (accessFlags[1] & 0x08) |
                    (accessFlags[1] & 0x10);
            if (pubStaFin != (0x01 | 0x08 | 0x10)) {
                throw new ClassFormatError();
            }
            // they may have their ACC_SYNTHETIC flag set and must not have any
            // of the other flags set
            int otherThanSyn = (accessFlags[1] & 0x02) |
                    (accessFlags[1] & 0x04) |
                    (accessFlags[1] & 0x40) |
                    (accessFlags[1] & 0x80) |
                    (accessFlags[0] & 0x40);
            if (otherThanSyn != 0) {
                throw new ClassFormatError();
            }
        }
    }

    public String checkNameIndex(ConstantPool constantPool, int nameIndex) {
        List<Constant> constants = constantPool.getPool();
        Constant constUtf8 = constants.get(nameIndex - 1);
        if (!(constUtf8 instanceof ConstantUtf8)) {
            throw new ClassFormatError();
        }
        return ((ConstantUtf8) constUtf8).getValue();
    }

    public String checkDescIndex(ConstantPool constantPool, int descIndex) {
        List<Constant> constants = constantPool.getPool();
        Constant constUtf8 = constants.get(descIndex - 1);
        if (!(constUtf8 instanceof ConstantUtf8)) {
            throw new ClassFormatError();
        }
        return ((ConstantUtf8) constUtf8).getValue();
    }
}
