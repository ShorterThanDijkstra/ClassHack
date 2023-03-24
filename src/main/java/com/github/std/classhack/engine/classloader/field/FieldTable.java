package com.github.std.classhack.engine.classloader.field;

import com.github.std.classhack.engine.classloader.ClassFile;
import com.github.std.classhack.engine.classloader.attribute.AttributeTable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.classhack.engine.classloader.field.FieldInfoChecker.*;
import static com.github.std.classhack.engine.util.BytesReader.readBytes2;

public class FieldTable {
    private final List<FieldInfo> fieldInfos;

    public FieldTable(List<FieldInfo> fieldInfos) {
        this.fieldInfos = fieldInfos;
    }


    public static FieldTable parse(int fieldsCount, InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        List<FieldInfo> fieldInfos = new ArrayList<>(fieldsCount);
        for (int i = 0; i < fieldsCount; i++) {
            fieldInfos.add(parseFieldInfo(input, metaData));
        }
        return new FieldTable(fieldInfos);
    }

    private static FieldInfo parseFieldInfo(InputStream input,
                                            ClassFile.ClassFileBuilder metaData) throws IOException {

        FieldInfo.FieldInfoBuilder builder = new FieldInfo.FieldInfoBuilder();

        parseFieldInfoAccessFlags(input, metaData, builder);
        parseFieldInfoName(input, metaData, builder);
        parseFieldInfoDescriptor(input, metaData, builder);
        parseFieldInfoAttrs(input, metaData, builder);

        return builder.build();
    }

    private static void parseFieldInfoAccessFlags(InputStream input,
                                                  ClassFile.ClassFileBuilder metaData,
                                                  FieldInfo.FieldInfoBuilder builder) throws IOException {
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

        checkAccessFlags(bsAccessFlags, metaData);
        builder.accessFlags = accessFlags;
    }

    private static void parseFieldInfoName(InputStream input,
                                           ClassFile.ClassFileBuilder metaData,
                                           FieldInfo.FieldInfoBuilder builder) throws IOException {
        int nameIndex = readBytes2(input);
        builder.name = checkNameIndex(metaData.constantPool, nameIndex);
    }

    private static void parseFieldInfoDescriptor(InputStream input,
                                                 ClassFile.ClassFileBuilder metaData,
                                                 FieldInfo.FieldInfoBuilder builder) throws IOException {
        int descIndex = readBytes2(input);
        builder.descriptor = checkDescIndex(metaData.constantPool, descIndex);
    }

    private static void parseFieldInfoAttrs(InputStream input,
                                            ClassFile.ClassFileBuilder metaData,
                                            FieldInfo.FieldInfoBuilder builder) throws IOException {
        int attrCount = readBytes2(input);
        builder.attributeTable = AttributeTable.parse(attrCount, input, metaData);
    }

    public List<FieldInfo> getFieldInfos() {
        return fieldInfos;
    }
}
