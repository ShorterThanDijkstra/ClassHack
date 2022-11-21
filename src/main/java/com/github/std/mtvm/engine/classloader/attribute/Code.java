package com.github.std.mtvm.engine.classloader.attribute;


import com.github.std.mtvm.engine.classloader.ClassFile;
import com.github.std.mtvm.engine.classloader.constant.Constant;
import com.github.std.mtvm.engine.classloader.constant.ConstantClass;
import com.github.std.mtvm.engine.classloader.constant.ConstantPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtvm.engine.classloader.attribute.AttributeTable.getAttrLength;
import static com.github.std.mtvm.engine.util.ByteUtil.byteArrayToInt;

public final class Code implements AttributeInfo {
    private final int maxStack;
    private final int maxLocals;
    private final byte[] opcodes;
    private final List<ExceptionInfo> exceptionTable;
    private final AttributeTable attributeTable;

    public int getMaxStack() {
        return maxStack;
    }

    public int getMaxLocals() {
        return maxLocals;
    }

    public byte[] getOpcodes() {
        return opcodes;
    }

    public List<ExceptionInfo> getExceptionTable() {
        return exceptionTable;
    }

    public AttributeTable getAttributeTable() {
        return attributeTable;
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

    private int parseMaxStack(InputStream input) throws IOException {
        byte[] bsMaxStack = new byte[2];
        int read = input.read(bsMaxStack);
        assert read == 2;
        return byteArrayToInt(bsMaxStack);
    }

    private int parseMaxLocals(InputStream input) throws IOException {
        byte[] bsMaxLocals = new byte[2];
        int read = input.read(bsMaxLocals);
        assert read == 2;
        return byteArrayToInt(bsMaxLocals);
    }

    private byte[] parseOpcodes(InputStream input) throws IOException {
        byte[] bsCodeLen = new byte[4];
        int read = input.read(bsCodeLen);
        assert read == 4;
        int codeLen = byteArrayToInt(bsCodeLen);

        byte[] opcodes = new byte[codeLen];
        read = input.read(opcodes);
        assert read == codeLen;
        return opcodes;
    }

    public Code(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        getAttrLength(input);
        this.maxStack = parseMaxStack(input);
        this.maxLocals = parseMaxLocals(input);
        this.opcodes = parseOpcodes(input);
        this.exceptionTable = parseExceptionTable(input, metaData.constantPool);
        this.attributeTable = parseAttrTable(input, metaData);
    }

    private AttributeTable parseAttrTable(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        byte[] bsAttrCount = new byte[2];
        int read = input.read(bsAttrCount);
        assert read == 2;

        int attrCount = byteArrayToInt(bsAttrCount);
        return new AttributeTable(attrCount, input, metaData);
    }

    private List<ExceptionInfo> parseExceptionTable(InputStream input, ConstantPool constantPool) throws IOException {
        byte[] bsTableLen = new byte[2];
        int read = input.read(bsTableLen);
        assert read == 2;

        int tableLen = byteArrayToInt(bsTableLen);
        List<ExceptionInfo> table = new ArrayList<>(tableLen);

        for (int i = 0; i < tableLen; i++) {
            table.add(parseExceptionInfo(input, constantPool));
        }
        return table;
    }

    private ExceptionInfo parseExceptionInfo(InputStream input, ConstantPool constantPool) throws IOException {
        byte[] bsStart = new byte[2];
        byte[] bsEnd = new byte[2];
        byte[] bsHandler = new byte[2];
        byte[] bsCatch = new byte[2];

        int read = input.read(bsStart);
        assert read == 2;
        read = input.read(bsEnd);
        assert read == 2;
        read = input.read(bsHandler);
        assert read == 2;
        read = input.read(bsCatch);
        assert read == 2;

        int startPc = byteArrayToInt(bsStart);
        int endPc = byteArrayToInt(bsEnd);
        int handlerPc = byteArrayToInt(bsHandler);
        int cacheTypeIndex = byteArrayToInt(bsCatch);

        Constant catchType = constantPool.getPool().get(cacheTypeIndex - 1);
        if (!(catchType instanceof ConstantClass)) {
            throw new ClassFormatError();
        }
        return new ExceptionInfo(startPc, endPc, handlerPc, (ConstantClass) catchType);
    }
}