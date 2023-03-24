package com.github.std.classhack.engine.classloader;

import com.github.std.classhack.engine.classloader.attribute.AttributeTable;
import com.github.std.classhack.engine.classloader.constant.ConstantPool;
import com.github.std.classhack.engine.classloader.field.FieldTable;
import com.github.std.classhack.engine.classloader.method.MethodTable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.classhack.engine.util.BytesReader.readBytes2;

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
        builder.minorVersion = readBytes2(input);
    }

    private void parseMajorVersion() throws IOException {
        builder.majorVersion = readBytes2(input);
    }


    private void parseConstantPool() throws IOException {
        int constantPoolCount = readBytes2(input);
        builder.constantPool = ConstantPool.parse(constantPoolCount - 1, input);
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
        int thisClassIndex = readBytes2(input);
        ConstantPool constantPool = builder.constantPool;
        builder.thisClass = checker.checkThisClass(constantPool, thisClassIndex);
    }

    private void parseSuperClass() throws IOException {
        int superClassIndex = readBytes2(input);
        builder.superClass = checker.checkSuperClass(builder.constantPool, superClassIndex, builder.isInterface());

    }

    private void parseInterfaces() throws IOException {
        int interfacesNum = readBytes2(input);
        int[] constClassIndices = new int[interfacesNum];
        for (int i = 0; i < interfacesNum; i++) {
            constClassIndices[i] = readBytes2(input);
        }
        builder.interfaces = checker.checkInterfaces(builder.constantPool, constClassIndices);
    }

    private void parseFields() throws IOException {
        int fieldsCount = readBytes2(input);
        builder.fieldTable = FieldTable.parse(fieldsCount, input, builder);
    }

    private void parseMethods() throws IOException {
        int methodCount = readBytes2(input);

        builder.methodTable = MethodTable.parse(methodCount, input, builder);
    }

    private void parseAttributes() throws IOException {
        int attrCount = readBytes2(input);

        builder.attributeTable = AttributeTable.parse(attrCount, input, builder);
    }


    public ClassFile getClassFile() {
        return builder.build();
    }

}
