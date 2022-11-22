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
import static com.github.std.mtvm.engine.util.BytesReader.readBytes2;
import static com.github.std.mtvm.engine.util.BytesReader.readBytes4;

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
        return readBytes2(input);
    }

    private int parseMaxLocals(InputStream input) throws IOException {
        return readBytes2(input);

    }

    private byte[] parseOpcodes(InputStream input) throws IOException {
        int codeLen = (int) readBytes4(input);

        byte[] opcodes = new byte[codeLen];
        int read = input.read(opcodes);
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

        int attrCount = readBytes2(input);
        return new AttributeTable(attrCount, input, metaData);
    }

    private List<ExceptionInfo> parseExceptionTable(InputStream input, ConstantPool constantPool) throws IOException {
        int tableLen = readBytes2(input);
        List<ExceptionInfo> table = new ArrayList<>(tableLen);

        for (int i = 0; i < tableLen; i++) {
            table.add(parseExceptionInfo(input, constantPool));
        }
        return table;
    }

    private ExceptionInfo parseExceptionInfo(InputStream input, ConstantPool constantPool) throws IOException {
        int startPc =readBytes2(input);
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