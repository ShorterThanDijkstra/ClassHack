package com.github.std.mtjvm.engine.classloader;

import com.github.std.mtjvm.engine.classloader.attribute.AttributeTable;
import com.github.std.mtjvm.engine.classloader.constant.ConstantPool;
import com.github.std.mtjvm.engine.classloader.field.FieldTable;
import com.github.std.mtjvm.engine.classloader.method.MethodTable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtjvm.engine.util.ByteUtil.byteArrayToInt;

public final class ClassReader implements Closeable {
    private final InputStream input;
    private final ClassFileBuilder builder;
    private final ClassFormatChecker checker;

    @Override
    public void close() throws IOException {
        input.close();
    }

    private static class ClassFileBuilder {
        private int minorVersion;
        private int majorVersion;
        private ConstantPool constantPool;
        private List<String> accessFlags;
        private String thisClass;
        private String superClass;
        private String[] interfaces;
        private FieldTable fieldTable;
        private MethodTable methodTable;
        private AttributeTable attributeTable;

        public ClassFile build() {
            return new ClassFile(
                    minorVersion,
                    majorVersion,
                    constantPool,
                    accessFlags,
                    thisClass,
                    superClass,
                    interfaces,
                    fieldTable,
                    methodTable,
                    attributeTable
            );
        }

        public boolean isInterface() {
            return accessFlags.contains("ACC_INTERFACE");
        }
    }

    public ClassReader(InputStream input) throws IOException {
        this.input = input;
        this.builder = new ClassFileBuilder();
        this.checker = new ClassFormatChecker();
        parseClassFile();

        input.close();
    }

    private void parseClassFile() throws IOException {
        parseMagicNum();
        parseMinorVersion();
        parseMajorVersion();
        parseConstantPool();
        parseAccessFlags();
        parseThisClass();
        parseSuperClass();
        parseInterfaces();
//        parseFields();
    }

    private void parseMagicNum() throws IOException {
        byte[] magicNum = new byte[4];
        int read = input.read(magicNum);
        assert read == 4;
        checker.validateMagicNum(magicNum);
    }


    private void parseMinorVersion() throws IOException {
        byte[] minorVersion = new byte[2];
        int read = input.read(minorVersion);
        assert read == 2;

        builder.minorVersion =
                byteArrayToInt(minorVersion);
    }

    private void parseMajorVersion() throws IOException {
        byte[] majorVersion = new byte[2];
        int read = input.read(majorVersion);
        assert read == 2;

        builder.majorVersion =
                byteArrayToInt(majorVersion);
    }


    private void parseConstantPool() throws IOException {
        byte[] bsConstPoolCount = new byte[2];
        int read = input.read(bsConstPoolCount);
        assert read == 2;

        int constantPoolCount = byteArrayToInt(bsConstPoolCount);
        builder.constantPool = new ConstantPool(
                constantPoolCount - 1,
                input
        );
    }

    private void parseAccessFlags() throws IOException {
        byte[] bsAccessFlags = new byte[2];
        int read = input.read(bsAccessFlags);
        assert read == 2;

        List<String> accessFlags = new ArrayList<>();
        if ((bsAccessFlags[1] & 0x01) == 0x01) {
            accessFlags.add("ACC_PUBLIC");
        }
        if ((bsAccessFlags[1] & 0x10) == 0x10) {
            accessFlags.add("ACC_FINAL");
        }
        if ((bsAccessFlags[1] & 0x20) == 0x20) {
            accessFlags.add("ACC_SUPER");
        }
        if ((bsAccessFlags[0] & 0x02) == 0x02) {
            accessFlags.add("ACC_INTERFACE");
        }
        if ((bsAccessFlags[0] & 0x04) == 0x04) {
            accessFlags.add("ACC_ABSTRACT");
        }
        if ((bsAccessFlags[0] & 0x10) == 0x10) {
            accessFlags.add("ACC_SYNTHETIC");
        }
        if ((bsAccessFlags[0] & 0x20) == 0x20) {
            accessFlags.add("ACC_ANNOTATION");
        }
        if ((bsAccessFlags[0] & 0x40) == 0x40) {
            accessFlags.add("ACC_ENUM");
        }
        builder.accessFlags = accessFlags;
    }

    private void parseThisClass() throws IOException {
        byte[] bsThisClass = new byte[2];
        int read = input.read(bsThisClass);
        assert read == 2;

        int thisClassIndex = byteArrayToInt(bsThisClass);
        ConstantPool constantPool = builder.constantPool;
        builder.thisClass = checker.validateThisClass(constantPool, thisClassIndex);
    }

    private void parseSuperClass() throws IOException {
        byte[] bsSuperClass = new byte[2];
        int read = input.read(bsSuperClass);
        assert read == 2;

        int superClassIndex = byteArrayToInt(bsSuperClass);

        builder.superClass = checker.validateSuperClass(
                builder.constantPool,
                superClassIndex,
                builder.isInterface()
        );

    }

    private void parseInterfaces() throws IOException {
        byte[] bsInterfacesNum = new byte[2];
        int read = input.read(bsInterfacesNum);
        assert read == 2;
        int interfacesNum = byteArrayToInt(bsInterfacesNum);

        int[] constClassIndices = new int[interfacesNum];
        for (int i = 0; i < interfacesNum; i++) {
            byte[] bsConstClassIndex = new byte[2];
            read = input.read(bsConstClassIndex);
            assert read == 2;
            constClassIndices[i] = byteArrayToInt(bsConstClassIndex);
        }
        builder.interfaces = checker.validateInterfaces(
                builder.constantPool,
                constClassIndices
        );
    }

    private void parseFields() throws IOException {
        byte[] bsFieldsNum = new byte[2];
        int read = input.read(bsFieldsNum);
        assert read == 2;

        int fieldsCount = byteArrayToInt(bsFieldsNum);
        builder.fieldTable = new FieldTable(
                fieldsCount,
                input,
                builder.constantPool,
                builder.isInterface()
        );
    }

    public ClassFile getClassFile() {
        return builder.build();
    }
}
