package com.github.std.classhack.engine.classloader.attribute;


import com.github.std.classhack.engine.classloader.ClassFile;
import com.github.std.classhack.engine.classloader.constant.Constant;
import com.github.std.classhack.engine.classloader.constant.ConstantClass;
import com.github.std.classhack.engine.classloader.constant.ConstantPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.classhack.engine.classloader.attribute.AttributeTable.getAttrLen;
import static com.github.std.classhack.engine.util.BytesReader.readBytes2;
import static com.github.std.classhack.engine.util.BytesReader.readBytes4;

public final class Code implements AttributeInfo {
    private final int maxStack;
    private final int maxLocals;
    private final List<Opcode> opcodes;
    private final List<ExceptionInfo> exceptionTable;
    private final AttributeTable attributeTable;

    public int getMaxStack() {
        return maxStack;
    }

    public int getMaxLocals() {
        return maxLocals;
    }

    public List<Opcode> getOpcodes() {
        return opcodes;
    }

    public List<ExceptionInfo> getExceptionTable() {
        return exceptionTable;
    }

    public AttributeTable getAttributeTable() {
        return attributeTable;
    }

    public static class Opcode {
        private static final byte[] EMPTY_OPERANDS = new byte[0];
        private final String mnemonic;
        private final byte value;
        private final byte[] operands;

        public Opcode(String mnemonic, byte value, byte[] operands) {
            this.mnemonic = mnemonic;
            this.value = value;
            this.operands = operands;
        }

        public Opcode(String mnemonic, byte value) {
            this.mnemonic = mnemonic;
            this.value = value;
            this.operands = EMPTY_OPERANDS;
        }

        public static Opcode parse(byte[] values, int pos) {
            byte value = values[pos];
            if ((value & 0xff) == 0x32) {
                return new Opcode("aaload", value);
            }
            if ((value & 0xff) == 0x53) {
                return new Opcode("aastore", value);
            }
            if ((value & 0xff) == 0x1) {
                return new Opcode("aconst_null", value);
            }
            if ((value & 0xff) == 0x19) {
                return new Opcode(
                        "aload",
                        value,
                        new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0x2a) {
                return new Opcode("aload_0", value);
            }
            if ((value & 0xff) == 0x2b) {
                return new Opcode("aload_1", value);
            }
            if ((value & 0xff) == 0x2c) {
                return new Opcode("aload_2", value);
            }
            if ((value & 0xff) == 0x2d) {
                return new Opcode("aload_3", value);
            }
            if ((value & 0xff) == 0xbd) {
                return new Opcode(
                        "anewarray",
                        value,
                        new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xb0) {
                return new Opcode("areturn", value);
            }
            if ((value & 0xff) == 0xbe) {
                return new Opcode("arraylength", value);
            }
            if ((value & 0xff) == 0x3a) {
                return new Opcode(
                        "astore",
                        value,
                        new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0x4b) {
                return new Opcode("astore_0", value);
            }
            if ((value & 0xff) == 0x4c) {
                return new Opcode("astore_1", value);
            }
            if ((value & 0xff) == 0x4d) {
                return new Opcode("astore_2", value);
            }
            if ((value & 0xff) == 0x4e) {
                return new Opcode("astore_3", value);
            }
            if ((value & 0xff) == 0xbf) {
                return new Opcode("athrow", value);
            }
            if ((value & 0xff) == 0x33) {
                return new Opcode("baload", value);
            }
            if ((value & 0xff) == 0x54) {
                return new Opcode("bastore", value);
            }
            if ((value & 0xff) == 0x10) {
                return new Opcode(
                        "bipush",
                        value,
                        new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0x34) {
                return new Opcode("caload", value);
            }
            if ((value & 0xff) == 0x55) {
                return new Opcode("castore", value);
            }
            if ((value & 0xff) == 0xc0) {
                return new Opcode(
                        "checkcast",
                        value,
                        new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0x90) {
                return new Opcode("d2f", value);
            }
            if ((value & 0xff) == 0x8e) {
                return new Opcode("d2i", value);
            }
            if ((value & 0xff) == 0x8f) {
                return new Opcode("d2l", value);
            }
            if ((value & 0xff) == 0x63) {
                return new Opcode("dadd", value);
            }
            if ((value & 0xff) == 0x31) {
                return new Opcode("daload", value);
            }
            if ((value & 0xff) == 0x52) {
                return new Opcode("dastore", value);
            }
            if ((value & 0xff) == 0x98) {
                return new Opcode("dcmpg", value);
            }
            if ((value & 0xff) == 0x97) {
                return new Opcode("dcmpl", value);
            }
            if ((value & 0xff) == 0xe) {
                return new Opcode("dconst_0", value);
            }
            if ((value & 0xff) == 0xf) {
                return new Opcode("dconst_1", value);
            }
            if ((value & 0xff) == 0x6f) {
                return new Opcode("ddiv", value);
            }

            throw new ClassFormatError("Unknown Opcode: " + Integer.toHexString(value & 0xff));
        }

        public String getMnemonic() {
            return mnemonic;
        }

        public byte getValue() {
            return value;
        }

        public byte[] getOperands() {
            return operands;
        }
    }

    private static class ExceptionInfo {
        private final int startPc;
        private final int endPc;
        private final int handlerPc;
        private final ConstantClass catchType;

        public ExceptionInfo(int startPc, int endPc, int handlerPc, ConstantClass catchType) {
            this.startPc = startPc;
            this.endPc = endPc;
            this.handlerPc = handlerPc;
            this.catchType = catchType;
        }

        public int getStartPc() {
            return startPc;
        }

        public int getEndPc() {
            return endPc;
        }

        public int getHandlerPc() {
            return handlerPc;
        }

        public ConstantClass getCatchType() {
            return catchType;
        }
    }

    private Code(int maxStack, int maxLocals, List<Opcode> opcodes, List<ExceptionInfo> exceptionTable, AttributeTable attributeTable) {
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
        this.opcodes = opcodes;
        this.exceptionTable = exceptionTable;
        this.attributeTable = attributeTable;
    }

    private static int parseMaxStack(InputStream input) throws IOException {
        return readBytes2(input);
    }

    private static int parseMaxLocals(InputStream input) throws IOException {
        return readBytes2(input);

    }

    private static List<Opcode> parseOpcodes(InputStream input) throws IOException {
        int codesLen = (int) readBytes4(input);

        byte[] values = new byte[codesLen];
        int read = input.read(values);
        assert read == codesLen;

        List<Opcode> opcodes = new ArrayList<>();
        int pos = 0;
        while (pos <= codesLen) {
            Opcode opcode = Opcode.parse(values, pos);
            opcodes.add(opcode);
            pos = pos + opcode.operands.length + 1;
        }

        return opcodes;
    }

    public static Code parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        getAttrLen(input);
        int maxStack = parseMaxStack(input);
        int maxLocals = parseMaxLocals(input);
        List<Opcode> opcodes = parseOpcodes(input);
        List<ExceptionInfo> exceptionTable = parseExceptionTable(input, metaData.constantPool);
        AttributeTable attributeTable = parseAttrTable(input, metaData);
        return new Code(maxStack, maxLocals, opcodes, exceptionTable, attributeTable);
    }

    private static AttributeTable parseAttrTable(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {

        int attrCount = readBytes2(input);
        return AttributeTable.parse(attrCount, input, metaData);
    }

    private static List<ExceptionInfo> parseExceptionTable(InputStream input, ConstantPool constantPool) throws IOException {
        int tableLen = readBytes2(input);
        List<ExceptionInfo> table = new ArrayList<>(tableLen);

        for (int i = 0; i < tableLen; i++) {
            table.add(parseExceptionInfo(input, constantPool));
        }
        return table;
    }

    private static ExceptionInfo parseExceptionInfo(InputStream input, ConstantPool constantPool) throws IOException {
        int startPc = readBytes2(input);
        int endPc = readBytes2(input);
        int handlerPc = readBytes2(input);
        int cacheTypeIndex = readBytes2(input);
        Constant catchType = constantPool.getPool().get(cacheTypeIndex - 1);
        if (!(catchType instanceof ConstantClass)) {
            throw new ClassFormatError();
        }
        return new ExceptionInfo(startPc, endPc, handlerPc, (ConstantClass) catchType);
    }
}