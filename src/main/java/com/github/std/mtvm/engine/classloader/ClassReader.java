package com.github.std.mtvm.engine.classloader;

import com.github.std.mtvm.engine.classloader.attribute.AttributeTable;
import com.github.std.mtvm.engine.classloader.constant.ConstantPool;
import com.github.std.mtvm.engine.classloader.field.FieldTable;
import com.github.std.mtvm.engine.classloader.method.MethodTable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtvm.engine.util.ByteUtil.byteArrayToInt;

public final class ClassReader implements Closeable {
    private final InputStream input;
    private final ClassFile.ClassFileBuilder builder;
    private final ClassFormatChecker checker;

    @Override
    public void close() throws IOException {
        int read = input.read();
        if (read != -1) {
            throw new ClassFormatError();
        }
        input.close();
    }

    public ClassReader(InputStream input) throws IOException {
        this.input = input;
        this.builder = new ClassFile.ClassFileBuilder();
        this.checker = new ClassFormatChecker();

        parseClassFile();

        close();
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
        parseFields();
        parseMethods();
        parseAttributes();
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

        builder.minorVersion = byteArrayToInt(minorVersion);
    }

    private void parseMajorVersion() throws IOException {
        byte[] majorVersion = new byte[2];
        int read = input.read(majorVersion);
        assert read == 2;

        builder.majorVersion = byteArrayToInt(majorVersion);
    }


    private void parseConstantPool() throws IOException {
        byte[] bsConstPoolCount = new byte[2];
        int read = input.read(bsConstPoolCount);
        assert read == 2;

        int constantPoolCount = byteArrayToInt(bsConstPoolCount);
        builder.constantPool = new ConstantPool(constantPoolCount - 1, input);
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
        checker.checkAccessFlags(bsAccessFlags);
        builder.accessFlags = accessFlags;
    }

    private void parseThisClass() throws IOException {
        byte[] bsThisClass = new byte[2];
        int read = input.read(bsThisClass);
        assert read == 2;

        int thisClassIndex = byteArrayToInt(bsThisClass);
        ConstantPool constantPool = builder.constantPool;
        builder.thisClass = checker.checkThisClass(constantPool, thisClassIndex);
    }

    private void parseSuperClass() throws IOException {
        byte[] bsSuperClass = new byte[2];
        int read = input.read(bsSuperClass);
        assert read == 2;

        int superClassIndex = byteArrayToInt(bsSuperClass);

        builder.superClass = checker.checkSuperClass(builder.constantPool, superClassIndex, builder.isInterface());

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
        builder.interfaces = checker.checkInterfaces(builder.constantPool, constClassIndices);
    }

    private void parseFields() throws IOException {
        byte[] bsFieldsNum = new byte[2];
        int read = input.read(bsFieldsNum);
        assert read == 2;

        int fieldsCount = byteArrayToInt(bsFieldsNum);
        builder.fieldTable = new FieldTable(fieldsCount, input, builder);
    }

    private void parseMethods() throws IOException {
        byte[] bsMethodCount = new byte[2];
        int read = input.read(bsMethodCount);
        assert read == 2;

        int methodCount = byteArrayToInt(bsMethodCount);
        builder.methodTable = new MethodTable(methodCount, input, builder);
    }

    private void parseAttributes() throws IOException {
        byte[] bsAttrCount = new byte[2];
        int read = input.read(bsAttrCount);
        assert read == 2;

        int attrCount = byteArrayToInt(bsAttrCount);
        builder.attributeTable = new AttributeTable(attrCount, input, builder);
    }


    public ClassFile getClassFile() {
        return builder.build();
    }

}
