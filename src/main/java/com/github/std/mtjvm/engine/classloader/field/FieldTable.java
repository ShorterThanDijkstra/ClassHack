package com.github.std.mtjvm.engine.classloader.field;

import com.github.std.mtjvm.engine.classloader.attribute.AttributeTable;
import com.github.std.mtjvm.engine.classloader.constant.ConstantPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtjvm.engine.util.ByteUtil.byteArrayToInt;

public class FieldTable {
    private final List<FieldInfo> fieldInfos;

    private static class FieldInfoBuilder {
        private List<String> accessFlags;
        private String name;
        private String descriptor;
        private AttributeTable attributeTable;

        private FieldInfo build() {
            return new FieldInfo(accessFlags, name, descriptor, attributeTable);
        }
    }

    public FieldTable(int fieldsCount,
                      InputStream input,
                      ConstantPool constantPool,
                      boolean isInterface) throws IOException {
        this.fieldInfos = new ArrayList<>(fieldsCount);
        FieldInfoChecker checker = new FieldInfoChecker();
        for (int i = 0; i < fieldsCount; i++) {
            parseFieldInfo(input, constantPool, checker, isInterface);
        }
    }

    private void parseFieldInfo(InputStream input,
                                ConstantPool constantPool,
                                FieldInfoChecker checker,
                                boolean isInterface) throws IOException {

        FieldInfoBuilder builder = new FieldInfoBuilder();

        parseFieldInfoAccessFlags(input, builder, checker, isInterface);
        parseFieldInfoName(input, builder, constantPool, checker);
        parseFieldInfoDescriptor(input, builder, constantPool, checker);
        parseFieldInfoAttrs(input, builder, constantPool);

        fieldInfos.add(builder.build());
    }

    private void parseFieldInfoAccessFlags(InputStream input,
                                           FieldInfoBuilder builder,
                                           FieldInfoChecker checker,
                                           boolean isInterface) throws IOException {
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
        if ((bsAccessFlags[1] & 0x40) == 0x40) {
            accessFlags.add("ACC_VOLATILE");
        }
        if ((bsAccessFlags[1] & 0x80) == 0x80) {
            accessFlags.add("ACC_TRANSIENT");
        }
        if ((bsAccessFlags[0] & 0x10) == 0x10) {
            accessFlags.add("ACC_SYNTHETIC");
        }
        if ((bsAccessFlags[0] & 0x40) == 0x40) {
            accessFlags.add("ACC_ENUM");
        }

        checker.checkAccessFlags(bsAccessFlags, isInterface);
        builder.accessFlags = accessFlags;
    }

    private void parseFieldInfoName(InputStream input,
                                    FieldInfoBuilder builder,
                                    ConstantPool constantPool,
                                    FieldInfoChecker checker) throws IOException {
        byte[] bsNameIndex = new byte[2];
        int read = input.read(bsNameIndex);
        assert read == 2;

        int nameIndex = byteArrayToInt(bsNameIndex);
        builder.name = checker.checkNameIndex(constantPool, nameIndex);
    }

    private void parseFieldInfoDescriptor(InputStream input,
                                          FieldInfoBuilder builder,
                                          ConstantPool constantPool,
                                          FieldInfoChecker checker) throws IOException {
        byte[] bsDescIndex = new byte[2];
        int read = input.read(bsDescIndex);
        assert read == 2;

        int descIndex = byteArrayToInt(bsDescIndex);
        builder.descriptor = checker.checkDescIndex(constantPool, descIndex);
    }

    private void parseFieldInfoAttrs(InputStream input,
                                     FieldInfoBuilder builder,
                                     ConstantPool constantPool) {

    }

    public List<FieldInfo> getFieldInfos() {
        return fieldInfos;
    }
}
