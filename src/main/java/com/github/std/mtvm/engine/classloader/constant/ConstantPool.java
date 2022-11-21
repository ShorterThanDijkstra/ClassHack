package com.github.std.mtvm.engine.classloader.constant;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtvm.engine.util.ByteUtil.byteArrayToInt;

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
        byte[] bsBootIndex = new byte[2];
        byte[] bsNameAndTypeIndex = new byte[2];

        int read = input.read(bsBootIndex);
        assert read == 2;

        read = input.read(bsNameAndTypeIndex);
        assert read == 2;

        pool.add(new ConstantInvokeDynamic(
                byteArrayToInt(bsBootIndex),
                byteArrayToInt(bsNameAndTypeIndex)
        ));
    }

    private void parseMethodType(InputStream input) throws IOException {
        byte[] bsDesc = new byte[2];
        int read = input.read(bsDesc);
        assert read == 2;

        pool.add(new ConstantMethodType(
                byteArrayToInt(bsDesc)
        ));
    }

    private void parseMethodHandle(InputStream input) throws IOException {
        int refKind = input.read();
        assert refKind <= 9 && refKind >= 1;

        byte[] bsRefIndex = new byte[2];
        int read = input.read(bsRefIndex);
        assert read == 2;

        pool.add(new ConstantMethodHandle(
                refKind,
                byteArrayToInt(bsRefIndex)
        ));
    }

    private void parseUtf8(InputStream input) throws IOException {
        byte[] bsLen = new byte[2];
        int read = input.read(bsLen);
        assert read == 2;

        int len = byteArrayToInt(bsLen);
        byte[] bsUtf8 = new byte[len];
        read = input.read(bsUtf8);
        assert read == len;

        String value = new String(bsUtf8);

        pool.add(new ConstantUtf8(value));
    }

    private void parseNameAndType(InputStream input) throws IOException {
        byte[] bsName = new byte[2];
        byte[] bsDesc = new byte[2];

        int read = input.read(bsName);
        assert read == 2;
        read = input.read(bsDesc);
        assert read == 2;

        pool.add(new ConstantNameAndType(
                byteArrayToInt(bsName),
                byteArrayToInt(bsDesc)
        ));
    }

    private void parseDouble(InputStream input) throws IOException {
        byte[] highBytes = new byte[4];
        byte[] lowBytes = new byte[4];

        int read = input.read(highBytes);
        assert read == 4;

        read = input.read(lowBytes);
        assert read == 4;

        long bits = byteArrayToInt(highBytes);
        bits = bits << 32 | byteArrayToInt(lowBytes);
        double value = Double.longBitsToDouble(bits);

        pool.add(new ConstantDouble(value));
    }

    private void parseLong(InputStream input) throws IOException {
        byte[] highBytes = new byte[4];
        byte[] lowBytes = new byte[4];

        int read = input.read(highBytes);
        assert read == 4;

        read = input.read(lowBytes);
        assert read == 4;

        long value = byteArrayToInt(highBytes);
        value = value << 32 | byteArrayToInt(lowBytes);

        pool.add(new ConstantLong(value));

    }

    private void parseFloat(InputStream input) throws IOException {
        byte[] bs = new byte[4];
        int read = input.read(bs);
        assert read == 4;

        float value = Float.intBitsToFloat(byteArrayToInt(bs));
        pool.add(new ConstantFloat(value));
    }

    private void parseInteger(InputStream input) throws IOException {
        byte[] bs = new byte[4];
        int read = input.read(bs);
        assert read == 4;

        int value = byteArrayToInt(bs);
        pool.add(new ConstantInteger(value));
    }

    private void parseString(InputStream input) throws IOException {
        byte[] stringIndex = new byte[2];
        int read = input.read(stringIndex);
        assert read == 2;
        pool.add(new ConstantString(
                byteArrayToInt(stringIndex)
        ));
    }

    private void parseInterfaceMethodRef(InputStream input) throws IOException {
        byte[] classIndex = new byte[2];
        byte[] nameAndTypeIndex = new byte[2];

        int read = input.read(classIndex);
        assert read == 2;
        read = input.read(nameAndTypeIndex);
        assert read == 2;

        pool.add(new ConstantInterfaceMethodRef(
                byteArrayToInt(classIndex),
                byteArrayToInt(nameAndTypeIndex)
        ));
    }

    private void parseMethodRef(InputStream input) throws IOException {
        byte[] classIndex = new byte[2];
        byte[] nameAndTypeIndex = new byte[2];

        int read = input.read(classIndex);
        assert read == 2;
        read = input.read(nameAndTypeIndex);
        assert read == 2;

        pool.add(new ConstantMethodRef(
                byteArrayToInt(classIndex),
                byteArrayToInt(nameAndTypeIndex)
        ));
    }

    private void parseFieldRef(InputStream input) throws IOException {
        byte[] classIndex = new byte[2];
        byte[] nameAndTypeIndex = new byte[2];

        int read = input.read(classIndex);
        assert read == 2;
        read = input.read(nameAndTypeIndex);
        assert read == 2;

        pool.add(new ConstantFieldRef(
                byteArrayToInt(classIndex),
                byteArrayToInt(nameAndTypeIndex)
        ));
    }

    private void parseClass(InputStream input) throws IOException {
        byte[] nameIndex = new byte[2];
        int read = input.read(nameIndex);
        assert read == 2;
        pool.add(new ConstantClass(byteArrayToInt(nameIndex)));
    }

    public List<Constant> getPool() {
        return pool;
    }
}
