package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.ClassFile;
import com.github.std.mtvm.engine.classloader.constant.Constant;
import com.github.std.mtvm.engine.classloader.constant.ConstantPool;
import com.github.std.mtvm.engine.classloader.constant.ConstantUtf8;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtvm.engine.util.BytesReader.byteArrayToInt;
import static com.github.std.mtvm.engine.util.BytesReader.byteArrayToLong;
import static com.github.std.mtvm.engine.util.Logger.info;

public class AttributeTable {
    private final List<AttributeInfo> attributes;

    public static long getAttrLength(InputStream input) throws IOException {
        byte[] bsAttrLen = new byte[4];
        int read = input.read(bsAttrLen);
        assert read == 4;

        return byteArrayToLong(bsAttrLen);
    }

    public AttributeTable(int attrCount, InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        attributes = new ArrayList<>(attrCount);
        for (int i = 0; i < attrCount; i++) {
            parseAttr(input, metaData);
        }
    }

    private void parseAttr(InputStream input,
                           ClassFile.ClassFileBuilder metaData) throws IOException {
        String name = getAttrName(input, metaData.constantPool);
        if ("ConstantValue".equals(name)) {
            parseConstantValue(input, metaData.constantPool);
        } else if ("Code".equals(name)) {
            parseCode(input, metaData);
        } else if ("StackMapTable".equals(name)) {
            parseStackMapTable(input, metaData);
        } else {
            info("Unknown attribute: " + name + ", skipped");
            skip(input);
        }
    }

    private void parseStackMapTable(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        StackMapTable stackMapTable = new StackMapTable(input, metaData);
        attributes.add(stackMapTable);
    }

    private void skip(InputStream input) throws IOException {
        long length = getAttrLength(input);
        long skipped = input.skip(length);
        assert skipped == length;
    }

    private void parseCode(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        Code code = new Code(input, metaData);
        attributes.add(code);
    }

    private void parseConstantValue(InputStream input,
                                    ConstantPool constantPool) throws IOException {
        ConstantValue attr = new ConstantValue(input, constantPool);
        attributes.add(attr);
    }


    private String getAttrName(InputStream input,
                               ConstantPool constantPool) throws IOException {
        byte[] bsNameIndex = new byte[2];
        int read = input.read(bsNameIndex);
        assert read == 2;

        int nameIndex = byteArrayToInt(bsNameIndex);
        Constant constUtf8 = constantPool.getPool().get(nameIndex - 1);
        if (!(constUtf8 instanceof ConstantUtf8)) {
            throw new ClassFormatError();
        }
        return ((ConstantUtf8) constUtf8).getValue();
    }

    public List<AttributeInfo> getAttributes() {
        return attributes;
    }
}
