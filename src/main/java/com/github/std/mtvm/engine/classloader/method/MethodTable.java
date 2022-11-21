package com.github.std.mtvm.engine.classloader.method;

import com.github.std.mtvm.engine.classloader.ClassFile;
import com.github.std.mtvm.engine.classloader.attribute.AttributeTable;
import com.github.std.mtvm.engine.classloader.constant.ConstantPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtvm.engine.util.ByteUtil.byteArrayToInt;

public class MethodTable {
    private final List<MethodInfo> methodInfos;

    public MethodTable(int methodCount,
                       InputStream input,
                       ClassFile.ClassFileBuilder metaData) throws IOException {
        methodInfos = new ArrayList<>(methodCount);
        MethodInfoChecker checker = new MethodInfoChecker();
        for (int i = 0; i < methodCount; i++) {
            parseMethodInfo(input, metaData, checker);
        }
    }

    private void parseMethodInfo(InputStream input,
                                 ClassFile.ClassFileBuilder metaData,
                                 MethodInfoChecker checker) throws IOException {
        MethodInfo.MethodInfoBuilder builder = new MethodInfo.MethodInfoBuilder();

        parseMethodInfoAccessFlags(input, metaData, checker, builder);
        parseMethodInfoName(input, metaData, checker, builder);
        parseMethodInfoDesc(input, metaData, checker, builder);
        parseMethodInfoAttr(input, metaData, builder);

        methodInfos.add(builder.build());
    }

    private void parseMethodInfoAttr(InputStream input,
                                     ClassFile.ClassFileBuilder metaData,
                                     MethodInfo.MethodInfoBuilder builder) throws IOException {
        byte[] bsAttrCount = new byte[2];
        int read = input.read(bsAttrCount);
        assert read == 2;

        int attrCount = byteArrayToInt(bsAttrCount);
        builder.attributeTable = new AttributeTable(attrCount, input, metaData);
    }

    private void parseMethodInfoDesc(InputStream input,
                                     ClassFile.ClassFileBuilder metaData,
                                     MethodInfoChecker checker,
                                     MethodInfo.MethodInfoBuilder builder) throws IOException {
        byte[] bsDescIndex = new byte[2];
        int read = input.read(bsDescIndex);
        assert read == 2;

        int descIndex = byteArrayToInt(bsDescIndex);
        builder.descriptor = checker.checkDescIndex(descIndex, metaData.constantPool);
    }

    private void parseMethodInfoName(InputStream input,
                                     ClassFile.ClassFileBuilder metaData,
                                     MethodInfoChecker checker,
                                     MethodInfo.MethodInfoBuilder builder) throws IOException {
        byte[] bsNameIndex = new byte[2];
        int read = input.read(bsNameIndex);
        assert read == 2;

        int nameIndex = byteArrayToInt(bsNameIndex);
        builder.name = checker.checkNameIndex(nameIndex, metaData.constantPool);
    }

    private void parseMethodInfoAccessFlags(InputStream input,
                                            ClassFile.ClassFileBuilder metaData,
                                            MethodInfoChecker checker,
                                            MethodInfo.MethodInfoBuilder builder) throws IOException {
        byte[] bsAccessFlags = new byte[2];
        int read = input.read(bsAccessFlags);
        assert read == 2;

        List<String> accessFlags = new ArrayList<>();
        if ((bsAccessFlags[1] & 0x01) == 0x01) {
            accessFlags.add("ACC_PUBLIC");
        }
        if ((bsAccessFlags[1] & 0x02) == 0x02) {
            accessFlags.add("ACC_PRIVATE");
        }
        if ((bsAccessFlags[1] & 0x04) == 0x04) {
            accessFlags.add("ACC_PROTECTED");
        }
        if ((bsAccessFlags[1] & 0x08) == 0x08) {
            accessFlags.add("ACC_STATIC");
        }
        if ((bsAccessFlags[1] & 0x10) == 0x10) {
            accessFlags.add("ACC_FINAL");
        }
        if ((bsAccessFlags[1] & 0x20) == 0x20) {
            accessFlags.add("ACC_SYNCHRONIZED");
        }
        if ((bsAccessFlags[1] & 0x40) == 0x40) {
            accessFlags.add("ACC_BRIDGE");
        }
        if ((bsAccessFlags[1] & 0x80) == 0x80) {
            accessFlags.add("ACC_VARARGS");
        }
        if ((bsAccessFlags[0] & 0x01) == 0x01) {
            accessFlags.add("ACC_NATIVE");
        }
        if ((bsAccessFlags[0] & 0x04) == 0x04) {
            accessFlags.add("ACC_ABSTRACT");
        }
        if ((bsAccessFlags[0] & 0x08) == 0x08) {
            accessFlags.add("ACC_STRICT");
        }
        if ((bsAccessFlags[0] & 0x10) == 0x10) {
            accessFlags.add("ACC_SYNTHETIC");
        }

        checker.checkAccessFlags(bsAccessFlags, metaData);
        builder.accessFlags = accessFlags;
    }

    public List<MethodInfo> getMethodInfos() {
        return methodInfos;
    }
}
