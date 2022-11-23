package com.github.std.mtvm.engine.classloader.constant;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtvm.engine.util.BytesReader.*;

public class ConstantPool {
    public static final byte CONSTANT_Class = 7;
    public static final byte CONSTANT_Fieldref = 9;
    public static final byte CONSTANT_Methodref = 10;
    public static final byte CONSTANT_InterfaceMethodref = 11;
    public static final byte CONSTANT_String = 8;
    public static final byte CONSTANT_Integer = 3;
    public static final byte CONSTANT_Float = 4;
    public static final byte CONSTANT_Long = 5;
    public static final byte CONSTANT_Double = 6;
    public static final byte CONSTANT_NameAndType = 12;
    public static final byte CONSTANT_Utf8 = 1;
    public static final byte CONSTANT_MethodHandle = 15;
    public static final byte CONSTANT_MethodType = 16;
    public static final byte CONSTANT_InvokeDynamic = 18;

    private final List<Constant> pool;

    public ConstantPool(int poolSize, InputStream input) throws IOException {
        this.pool = new ArrayList<>(poolSize);
        parseData(poolSize, input);
    }

    private void parseData(int poolSize, InputStream input) throws IOException {
        int count = 0;

        while (count < poolSize) {
            int tag = input.read();
            count++;
            if (tag == CONSTANT_Class) {
                parseClass(input);
                continue;
            }
            if (tag == CONSTANT_Fieldref) {
                parseFieldRef(input);
                continue;
            }
            if (tag == CONSTANT_Methodref) {
                parseMethodRef(input);
                continue;
            }
            if (tag == CONSTANT_InterfaceMethodref) {
                parseInterfaceMethodRef(input);
                continue;
            }
            if (tag == CONSTANT_String) {
                parseString(input);
                continue;
            }
            if (tag == CONSTANT_Integer) {
                parseInteger(input);
                continue;
            }
            if (tag == CONSTANT_Float) {
                parseFloat(input);
                continue;
            }
            if (tag == CONSTANT_Long) {
                parseLong(input);
                continue;
            }
            if (tag == CONSTANT_Double) {
                parseDouble(input);
                continue;
            }
            if (tag == CONSTANT_NameAndType) {
                parseNameAndType(input);
                continue;
            }
            if (tag == CONSTANT_Utf8) {
                parseUtf8(input);
                continue;
            }
            if (tag == CONSTANT_MethodHandle) {
                parseMethodHandle(input);
                continue;
            }
            if (tag == CONSTANT_MethodType) {
                parseMethodType(input);
                continue;
            }
            if (tag == CONSTANT_InvokeDynamic) {
                parseInvokeDynamic(input);
                continue;
            }
            throw new ClassFormatError("Unsupported Constant Pool Tag");
        }
    }


    private void parseInvokeDynamic(InputStream input) throws IOException {
        int bootIndex = readBytes2(input);
        int nameAndTypeIndex = readBytes2(input);
        pool.add(new ConstantInvokeDynamic(
                bootIndex, nameAndTypeIndex
        ));
    }

    private void parseMethodType(InputStream input) throws IOException {
        int descIndex = readBytes2(input);
        pool.add(new ConstantMethodType(descIndex));
    }

    private void parseMethodHandle(InputStream input) throws IOException {
        int refKind = readByte(input);
        assert refKind <= 9 && refKind >= 1;

        int refIndex = readBytes2(input);
        pool.add(new ConstantMethodHandle(
                refKind,
                refIndex
        ));
    }

    private void parseUtf8(InputStream input) throws IOException {
        int len = readBytes2(input);
        byte[] bsUtf8 = new byte[len];
        int read = input.read(bsUtf8);
        assert read == len;

        String value = new String(bsUtf8);

        pool.add(new ConstantUtf8(value));
    }

    private void parseNameAndType(InputStream input) throws IOException {
        int nameIndex = readBytes2(input);
        int descIndex = readBytes2(input);
        pool.add(new ConstantNameAndType(
                nameIndex,
                descIndex
        ));
    }

    private void parseDouble(InputStream input) throws IOException {

        long bits = readBytes4(input);
        bits = bits << 32 | readBytes4(input);
        double value = Double.longBitsToDouble(bits);

        pool.add(new ConstantDouble(value));
    }

    private void parseLong(InputStream input) throws IOException {

        long value = readBytes4(input);
        value = value << 32 | readBytes4(input);
        pool.add(new ConstantLong(value));

    }

    private void parseFloat(InputStream input) throws IOException {
        int bits = (int) readBytes4(input);
        float value = Float.intBitsToFloat(bits);
        pool.add(new ConstantFloat(value));
    }

    private void parseInteger(InputStream input) throws IOException {
        int value = (int) readBytes4(input);
        pool.add(new ConstantInteger(value));
    }

    private void parseString(InputStream input) throws IOException {
        int strIndex = readBytes2(input);
        pool.add(new ConstantString(strIndex));
    }

    private void parseInterfaceMethodRef(InputStream input) throws IOException {
        int classIndex = readBytes2(input);
        int nameAndTypeIndex = readBytes2(input);
        pool.add(new ConstantInterfaceMethodRef(
                classIndex,
                nameAndTypeIndex
        ));
    }

    private void parseMethodRef(InputStream input) throws IOException {
        int classIndex = readBytes2(input);
        int nameAndTypeIndex = readBytes2(input);
        pool.add(new ConstantMethodRef(
                classIndex,
                nameAndTypeIndex
        ));
    }

    private void parseFieldRef(InputStream input) throws IOException {
        int classIndex = readBytes2(input);
        int nameAndTypeIndex = readBytes2(input);
        pool.add(new ConstantFieldRef(
                classIndex,
                nameAndTypeIndex
        ));
    }

    private void parseClass(InputStream input) throws IOException {
        int nameIndex = readBytes2(input);
        pool.add(new ConstantClass(nameIndex));
    }

    public List<Constant> getPool() {
        return pool;
    }

    public String getConstClassName(int index) {
        Constant constClass = pool.get(index - 1);
        if (!(constClass instanceof ConstantClass)) {
            throw new ClassFormatError();
        }
        int nameIndex = ((ConstantClass) constClass).getNameIndex();
        return getUtf8Str(nameIndex);
    }

    public String getUtf8Str(int index) {
        Constant constUtf8 = pool.get(index - 1);
        if (!(constUtf8 instanceof ConstantUtf8)) {
            throw new ClassFormatError();
        }
        return ((ConstantUtf8) constUtf8).getValue();
    }
}
