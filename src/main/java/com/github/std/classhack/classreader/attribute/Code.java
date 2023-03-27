package com.github.std.classhack.classreader.attribute;


import com.github.std.classhack.classreader.ClassFile;
import com.github.std.classhack.classreader.constant.Constant;
import com.github.std.classhack.classreader.constant.ConstantClass;
import com.github.std.classhack.classreader.constant.ConstantPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.classhack.classreader.util.BytesReader.*;

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

    public interface Opcode {

        String mnemonic();

        byte value();

        byte[] operands();

        String show();

        static void printOpcodes(Code code) {
            List<Code.Opcode> opcodes = code.getOpcodes();
            int line = 0;
            for (Code.Opcode opcode : opcodes) {
                System.out.print(line + ": ");
                System.out.println(opcode.show());
                line += opcode.operands().length + 1;
            }
        }

        static Opcode parse(final byte[] values, final int pos) {
            byte value = values[pos];
            // special cases: lookupswitch, tableswitch, wide
            if ((value & 0xff) == 0xab) {
                int end = pos + 1;
                while (end % 4 != 0) {
                    end++;
                }

                int defaultJump = byteArrayToInt(values, end, 4);
                end += 4; //default

                int npairs = byteArrayToInt(values, end, 4);
                end += 4;

                int[] matchOffsetPairs = new int[npairs * 2];
                for (int i = 0; i < npairs; i++) {
                    matchOffsetPairs[i * 2] = byteArrayToInt(values, end, 4);
                    end += 4;
                    matchOffsetPairs[i * 2 + 1] = byteArrayToInt(values, end, 4);
                    end += 4;
                }

                byte[] operands = new byte[end - (pos + 1)];
                for (int i = 0; i < operands.length; i++) {
                    operands[i] = values[i + pos + 1];
                }

                return new LookupSwitchOpcode("lookupswitch", value, operands, defaultJump, matchOffsetPairs);
            }

            if ((value & 0xff) == 0xaa) {
                int end = pos + 1;
                while (end % 4 != 0) {
                    end++;
                }

                int defaultJump = byteArrayToInt(values, end, 4);
                end += 4;

                int low = byteArrayToInt(values, end, 4);
                end += 4;

                int high = byteArrayToInt(values, end, 4);
                end += 4;

                int offset = high - low + 1;
                int[] offsets = new int[offset];
                for (int i = 0; i < offset; i++) {
                    offsets[i] = byteArrayToInt(values, end, 4);
                    end += 4;
                }

                byte[] operands = new byte[end - (pos + 1)];
                for (int i = 0; i < operands.length; i++) {
                    operands[i] = values[i + pos + 1];
                }

                return new TableSwitchOpcode("stableswitch", value, operands, defaultJump, low, high, offsets);
            }
            if ((value & 0xff) == 0xc4) {
                byte opcodeValue = values[pos + 1];
                int len = 3;
                if ((opcodeValue & 0xff) == 0x84) {
                    len = 5;
                }
                byte[] operands = new byte[len];
                for (int i = 0; i < len; i++) {
                    operands[i] = values[pos + 1 + i];
                }
                return new FixLengthOpcode("wide", value, operands);
            }
            if ((value & 0xff) == 0x32) {
                return new FixLengthOpcode("aaload", value);
            }
            if ((value & 0xff) == 0x53) {
                return new FixLengthOpcode("aastore", value);
            }
            if ((value & 0xff) == 0x1) {
                return new FixLengthOpcode("aconst_null", value);
            }
            if ((value & 0xff) == 0x19) {
                return new FixLengthOpcode("aload", value, new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0x2a) {
                return new FixLengthOpcode("aload_0", value);
            }
            if ((value & 0xff) == 0x2b) {
                return new FixLengthOpcode("aload_1", value);
            }
            if ((value & 0xff) == 0x2c) {
                return new FixLengthOpcode("aload_2", value);
            }
            if ((value & 0xff) == 0x2d) {
                return new FixLengthOpcode("aload_3", value);
            }
            if ((value & 0xff) == 0xbd) {
                return new FixLengthOpcode("anewarray", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xb0) {
                return new FixLengthOpcode("areturn", value);
            }
            if ((value & 0xff) == 0xbe) {
                return new FixLengthOpcode("arraylength", value);
            }
            if ((value & 0xff) == 0x3a) {
                return new FixLengthOpcode("astore", value, new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0x4c) {
                return new FixLengthOpcode("astore_1", value);
            }
            if ((value & 0xff) == 0x4d) {
                return new FixLengthOpcode("astore_2", value);
            }
            if ((value & 0xff) == 0x4e) {
                return new FixLengthOpcode("astore_3", value);
            }
            if ((value & 0xff) == 0xbf) {
                return new FixLengthOpcode("athrow", value);
            }
            if ((value & 0xff) == 0x33) {
                return new FixLengthOpcode("baload", value);
            }
            if ((value & 0xff) == 0x54) {
                return new FixLengthOpcode("bastore", value);
            }
            if ((value & 0xff) == 0x10) {
                return new FixLengthOpcode("bipush", value, new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0x34) {
                return new FixLengthOpcode("caload", value);
            }
            if ((value & 0xff) == 0x55) {
                return new FixLengthOpcode("castore", value);
            }
            if ((value & 0xff) == 0xc0) {
                return new FixLengthOpcode("checkcast", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0x90) {
                return new FixLengthOpcode("d2f", value);
            }
            if ((value & 0xff) == 0x8e) {
                return new FixLengthOpcode("d2i", value);
            }
            if ((value & 0xff) == 0x8f) {
                return new FixLengthOpcode("d2l", value);
            }
            if ((value & 0xff) == 0x63) {
                return new FixLengthOpcode("dadd", value);
            }
            if ((value & 0xff) == 0x31) {
                return new FixLengthOpcode("daload", value);
            }
            if ((value & 0xff) == 0x52) {
                return new FixLengthOpcode("dastore", value);
            }
            if ((value & 0xff) == 0x98) {
                return new FixLengthOpcode("dcmpg", value);
            }
            if ((value & 0xff) == 0x97) {
                return new FixLengthOpcode("dcmpl", value);
            }
            if ((value & 0xff) == 0xe) {
                return new FixLengthOpcode("dconst_0", value);
            }
            if ((value & 0xff) == 0xf) {
                return new FixLengthOpcode("dconst_1", value);
            }
            if ((value & 0xff) == 0x6f) {
                return new FixLengthOpcode("ddiv", value);
            }
            if ((value & 0xff) == 0x18) {
                return new FixLengthOpcode("dload", value, new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0x26) {
                return new FixLengthOpcode("dload_0", value);
            }
            if ((value & 0xff) == 0x27) {
                return new FixLengthOpcode("dload_1", value);
            }
            if ((value & 0xff) == 0x28) {
                return new FixLengthOpcode("dload_2", value);
            }
            if ((value & 0xff) == 0x29) {
                return new FixLengthOpcode("dload_3", value);
            }
            if ((value & 0xff) == 0x6b) {
                return new FixLengthOpcode("dmul", value);
            }
            if ((value & 0xff) == 0x77) {
                return new FixLengthOpcode("dneg", value);
            }
            if ((value & 0xff) == 0x73) {
                return new FixLengthOpcode("drem", value);
            }
            if ((value & 0xff) == 0xaf) {
                return new FixLengthOpcode("dreturn", value);
            }
            if ((value & 0xff) == 0x39) {
                return new FixLengthOpcode("dstore", value, new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0x47) {
                return new FixLengthOpcode("dstore_0", value);
            }
            if ((value & 0xff) == 0x48) {
                return new FixLengthOpcode("dstore_1", value);
            }
            if ((value & 0xff) == 0x49) {
                return new FixLengthOpcode("dstore_2", value);
            }
            if ((value & 0xff) == 0x4a) {
                return new FixLengthOpcode("dstore_3", value);
            }
            if ((value & 0xff) == 0x67) {
                return new FixLengthOpcode("dsub", value);
            }
            if ((value & 0xff) == 0x59) {
                return new FixLengthOpcode("dup", value);
            }
            if ((value & 0xff) == 0x5a) {
                return new FixLengthOpcode("dup_x1", value);
            }
            if ((value & 0xff) == 0x5b) {
                return new FixLengthOpcode("dup_x2", value);
            }
            if ((value & 0xff) == 0x5c) {
                return new FixLengthOpcode("dup2", value);
            }
            if ((value & 0xff) == 0x5d) {
                return new FixLengthOpcode("dup2_x1", value);
            }
            if ((value & 0xff) == 0x5e) {
                return new FixLengthOpcode("dup2_x2", value);
            }
            if ((value & 0xff) == 0x8d) {
                return new FixLengthOpcode("f2d", value);
            }
            if ((value & 0xff) == 0x8b) {
                return new FixLengthOpcode("f2i", value);
            }
            if ((value & 0xff) == 0x8c) {
                return new FixLengthOpcode("f2l", value);
            }
            if ((value & 0xff) == 0x62) {
                return new FixLengthOpcode("fadd", value);
            }
            if ((value & 0xff) == 0x30) {
                return new FixLengthOpcode("faload", value);
            }
            if ((value & 0xff) == 0x51) {
                return new FixLengthOpcode("fastore", value);
            }
            if ((value & 0xff) == 0x96) {
                return new FixLengthOpcode("fcmpg", value);
            }
            if ((value & 0xff) == 0x95) {
                return new FixLengthOpcode("fcmpl", value);
            }
            if ((value & 0xff) == 0xb) {
                return new FixLengthOpcode("fconst_0", value);
            }
            if ((value & 0xff) == 0xc) {
                return new FixLengthOpcode("fconst_1", value);
            }
            if ((value & 0xff) == 0xd) {
                return new FixLengthOpcode("fconst_2", value);
            }
            if ((value & 0xff) == 0x6e) {
                return new FixLengthOpcode("fdiv", value);
            }
            if ((value & 0xff) == 0x17) {
                return new FixLengthOpcode("fload", value, new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0x22) {
                return new FixLengthOpcode("fload_0", value);
            }
            if ((value & 0xff) == 0x23) {
                return new FixLengthOpcode("fload_1", value);
            }
            if ((value & 0xff) == 0x24) {
                return new FixLengthOpcode("fload_2", value);
            }
            if ((value & 0xff) == 0x25) {
                return new FixLengthOpcode("fload_3", value);
            }
            if ((value & 0xff) == 0x6a) {
                return new FixLengthOpcode("fmul", value);
            }
            if ((value & 0xff) == 0x76) {
                return new FixLengthOpcode("fneg", value);
            }
            if ((value & 0xff) == 0x72) {
                return new FixLengthOpcode("frem", value);
            }
            if ((value & 0xff) == 0xae) {
                return new FixLengthOpcode("freturn", value);
            }
            if ((value & 0xff) == 0x38) {
                return new FixLengthOpcode("fstore", value, new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0x43) {
                return new FixLengthOpcode("fstore_0", value);
            }
            if ((value & 0xff) == 0x44) {
                return new FixLengthOpcode("fstore_1", value);
            }
            if ((value & 0xff) == 0x45) {
                return new FixLengthOpcode("fstore_2", value);
            }
            if ((value & 0xff) == 0x46) {
                return new FixLengthOpcode("fstore_3", value);
            }
            if ((value & 0xff) == 0x66) {
                return new FixLengthOpcode("fsub", value);
            }
            if ((value & 0xff) == 0xb4) {
                return new FixLengthOpcode("getfield", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xb2) {
                return new FixLengthOpcode("getstatic", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xa7) {
                return new FixLengthOpcode("goto", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xc8) {
                return new FixLengthOpcode("goto_w", value, new byte[]{values[pos + 1], values[pos + 2], values[pos + 3], values[pos + 4]});
            }
            if ((value & 0xff) == 0x91) {
                return new FixLengthOpcode("i2b", value);
            }
            if ((value & 0xff) == 0x92) {
                return new FixLengthOpcode("i2c", value);
            }
            if ((value & 0xff) == 0x87) {
                return new FixLengthOpcode("i2d", value);
            }
            if ((value & 0xff) == 0x86) {
                return new FixLengthOpcode("i2f", value);
            }
            if ((value & 0xff) == 0x85) {
                return new FixLengthOpcode("i2l", value);
            }
            if ((value & 0xff) == 0x93) {
                return new FixLengthOpcode("i2s", value);
            }
            if ((value & 0xff) == 0x60) {
                return new FixLengthOpcode("iadd", value);
            }
            if ((value & 0xff) == 0x2e) {
                return new FixLengthOpcode("iaload", value);
            }
            if ((value & 0xff) == 0x7e) {
                return new FixLengthOpcode("iand", value);
            }
            if ((value & 0xff) == 0x4f) {
                return new FixLengthOpcode("iastore", value);
            }
            if ((value & 0xff) == 0x2) {
                return new FixLengthOpcode("iconst_m1", value);
            }
            if ((value & 0xff) == 0x3) {
                return new FixLengthOpcode("iconst_0", value);
            }
            if ((value & 0xff) == 0x4) {
                return new FixLengthOpcode("iconst_1", value);
            }
            if ((value & 0xff) == 0x5) {
                return new FixLengthOpcode("iconst_2", value);
            }
            if ((value & 0xff) == 0x6) {
                return new FixLengthOpcode("iconst_3", value);
            }
            if ((value & 0xff) == 0x7) {
                return new FixLengthOpcode("iconst_4", value);
            }
            if ((value & 0xff) == 0x8) {
                return new FixLengthOpcode("iconst_5", value);
            }
            if ((value & 0xff) == 0x6c) {
                return new FixLengthOpcode("idiv", value);
            }
            if ((value & 0xff) == 0xa5) {
                return new FixLengthOpcode("if_acmpeq", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xa6) {
                return new FixLengthOpcode("if_acmpne", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0x9f) {
                return new FixLengthOpcode("if_icmpeq", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xa0) {
                return new FixLengthOpcode("if_icmpne", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xa1) {
                return new FixLengthOpcode("if_icmplt", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xa2) {
                return new FixLengthOpcode("if_icmpge", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xa3) {
                return new FixLengthOpcode("if_icmpgt", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xa4) {
                return new FixLengthOpcode("if_icmple", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0x99) {
                return new FixLengthOpcode("ifeq", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0x9a) {
                return new FixLengthOpcode("ifne", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0x9b) {
                return new FixLengthOpcode("iflt", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0x9c) {
                return new FixLengthOpcode("ifge", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0x9d) {
                return new FixLengthOpcode("ifgt", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0x9e) {
                return new FixLengthOpcode("ifle", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xc7) {
                return new FixLengthOpcode("ifnonull", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xc6) {
                return new FixLengthOpcode("ifnull", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0x84) {
                return new FixLengthOpcode("iinc", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0x15) {
                return new FixLengthOpcode("iload", value, new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0x1a) {
                return new FixLengthOpcode("iload_0", value);
            }
            if ((value & 0xff) == 0x1b) {
                return new FixLengthOpcode("iload_1", value);
            }
            if ((value & 0xff) == 0x1c) {
                return new FixLengthOpcode("iload_2", value);
            }
            if ((value & 0xff) == 0x1d) {
                return new FixLengthOpcode("iload_3", value);
            }
            if ((value & 0xff) == 0x68) {
                return new FixLengthOpcode("imul", value);
            }
            if ((value & 0xff) == 0x74) {
                return new FixLengthOpcode("ineg", value);
            }
            if ((value & 0xff) == 0xc1) {
                return new FixLengthOpcode("instanceof", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xba) {
                return new FixLengthOpcode("invokedynamic", value, new byte[]{values[pos + 1], values[pos + 2], values[pos + 3], values[pos + 4]});
            }
            if ((value & 0xff) == 0xb9) {
                return new FixLengthOpcode("invokeinterface", value, new byte[]{values[pos + 1], values[pos + 2], values[pos + 3], values[pos + 4]});
            }
            if ((value & 0xff) == 0xb7) {
                return new FixLengthOpcode("invokespecial", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xb8) {
                return new FixLengthOpcode("invokestatic", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xb6) {
                return new FixLengthOpcode("invokevirtual", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0x80) {
                return new FixLengthOpcode("ior", value);
            }
            if ((value & 0xff) == 0x70) {
                return new FixLengthOpcode("irem", value);
            }
            if ((value & 0xff) == 0xac) {
                return new FixLengthOpcode("ireturn", value);
            }
            if ((value & 0xff) == 0x78) {
                return new FixLengthOpcode("ishl", value);
            }
            if ((value & 0xff) == 0x36) {
                return new FixLengthOpcode("istore", value, new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0x3b) {
                return new FixLengthOpcode("istore_0", value);
            }
            if ((value & 0xff) == 0x3c) {
                return new FixLengthOpcode("istore_1", value);
            }
            if ((value & 0xff) == 0x3d) {
                return new FixLengthOpcode("istore_2", value);
            }
            if ((value & 0xff) == 0x3e) {
                return new FixLengthOpcode("istore_3", value);
            }
            if ((value & 0xff) == 0x64) {
                return new FixLengthOpcode("isub", value);
            }
            if ((value & 0xff) == 0x7c) {
                return new FixLengthOpcode("iushr", value);
            }
            if ((value & 0xff) == 0x82) {
                return new FixLengthOpcode("ixor", value);
            }
            if ((value & 0xff) == 0xa8) {
                return new FixLengthOpcode("jsr", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xc9) {
                return new FixLengthOpcode("jsr_w", value, new byte[]{values[pos + 1], values[pos + 2], values[pos + 3], values[pos + 4]});
            }
            if ((value & 0xff) == 0x8a) {
                return new FixLengthOpcode("i2d", value);
            }
            if ((value & 0xff) == 0x89) {
                return new FixLengthOpcode("l2f", value);
            }
            if ((value & 0xff) == 0x88) {
                return new FixLengthOpcode("l2i", value);
            }
            if ((value & 0xff) == 0x61) {
                return new FixLengthOpcode("ladd", value);
            }
            if ((value & 0xff) == 0x2f) {
                return new FixLengthOpcode("laload", value);
            }
            if ((value & 0xff) == 0x7f) {
                return new FixLengthOpcode("land", value);
            }
            if ((value & 0xff) == 0x50) {
                return new FixLengthOpcode("lastore", value);
            }
            if ((value & 0xff) == 0x94) {
                return new FixLengthOpcode("lcmp", value);
            }
            if ((value & 0xff) == 0x9) {
                return new FixLengthOpcode("lconst_0", value);
            }
            if ((value & 0xff) == 0xa) {
                return new FixLengthOpcode("lconst_1", value);
            }
            if ((value & 0xff) == 0x12) {
                return new FixLengthOpcode("ldc", value, new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0x13) {
                return new FixLengthOpcode("ldc_w", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0x14) {
                return new FixLengthOpcode("ldc2_w", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0x6d) {
                return new FixLengthOpcode("ldiv", value);
            }
            if ((value & 0xff) == 0x16) {
                return new FixLengthOpcode("lload", value, new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0x1e) {
                return new FixLengthOpcode("lload_0", value);
            }
            if ((value & 0xff) == 0x1f) {
                return new FixLengthOpcode("lload_1", value);
            }
            if ((value & 0xff) == 0x20) {
                return new FixLengthOpcode("lload_2", value);
            }
            if ((value & 0xff) == 0x21) {
                return new FixLengthOpcode("lload_3", value);
            }
            if ((value & 0xff) == 0x69) {
                return new FixLengthOpcode("lmul", value);
            }
            if ((value & 0xff) == 0x75) {
                return new FixLengthOpcode("lneg", value);
            }
            if ((value & 0xff) == 0x81) {
                return new FixLengthOpcode("lor", value);
            }
            if ((value & 0xff) == 0x71) {
                return new FixLengthOpcode("lrem", value);
            }
            if ((value & 0xff) == 0xad) {
                return new FixLengthOpcode("lreturn", value);
            }
            if ((value & 0xff) == 0x79) {
                return new FixLengthOpcode("lshl", value);
            }
            if ((value & 0xff) == 0x7b) {
                return new FixLengthOpcode("lshr", value);
            }
            if ((value & 0xff) == 0x37) {
                return new FixLengthOpcode("lstore", value, new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0x3f) {
                return new FixLengthOpcode("lstore_0", value);
            }
            if ((value & 0xff) == 0x40) {
                return new FixLengthOpcode("lstore_1", value);
            }
            if ((value & 0xff) == 0x41) {
                return new FixLengthOpcode("lstore_2", value);
            }
            if ((value & 0xff) == 0x42) {
                return new FixLengthOpcode("lstore_3", value);
            }
            if ((value & 0xff) == 0x65) {
                return new FixLengthOpcode("lsub", value);
            }
            if ((value & 0xff) == 0x7d) {
                return new FixLengthOpcode("lushr", value);
            }
            if ((value & 0xff) == 0x83) {
                return new FixLengthOpcode("lxor", value);
            }
            if ((value & 0xff) == 0xc2) {
                return new FixLengthOpcode("monitorenter", value);
            }
            if ((value & 0xff) == 0xc3) {
                return new FixLengthOpcode("monitorexit", value);
            }
            if ((value & 0xff) == 0xc5) {
                return new FixLengthOpcode("multianewarray", value, new byte[]{values[pos + 1], values[pos + 2], values[pos + 3]});
            }
            if ((value & 0xff) == 0xbb) {
                return new FixLengthOpcode("new", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xbc) {
                return new FixLengthOpcode("newarray", value, new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0x0) {
                return new FixLengthOpcode("nop", value);
            }
            if ((value & 0xff) == 0x57) {
                return new FixLengthOpcode("pop", value);
            }
            if ((value & 0xff) == 0x58) {
                return new FixLengthOpcode("pop2", value);
            }
            if ((value & 0xff) == 0xb5) {
                return new FixLengthOpcode("putfield", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xb3) {
                return new FixLengthOpcode("putstatic", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0xa9) {
                return new FixLengthOpcode("ret", value, new byte[]{values[pos + 1]});
            }
            if ((value & 0xff) == 0xb1) {
                return new FixLengthOpcode("return", value);
            }
            if ((value & 0xff) == 0x35) {
                return new FixLengthOpcode("saload", value);
            }
            if ((value & 0xff) == 0x56) {
                return new FixLengthOpcode("satore", value);
            }
            if ((value & 0xff) == 0x11) {
                return new FixLengthOpcode("sipush", value, new byte[]{values[pos + 1], values[pos + 2]});
            }
            if ((value & 0xff) == 0x5f) {
                return new FixLengthOpcode("swap", value);
            }
            throw new ClassFormatError("Unknown Opcode: " + Integer.toHexString(value & 0xff));
        }

    }

    public static class LookupSwitchOpcode implements Opcode {
        private final String mnemonic;
        private final byte value;
        private final byte[] operands;
        private final int defaultJump;
        private final int[] matchOffsetPairs;

        public LookupSwitchOpcode(String mnemonic, byte value, byte[] operands, int defaultJump, int[] matchOffsetPairs) {
            this.mnemonic = mnemonic;
            this.value = value;
            this.operands = operands;
            this.defaultJump = defaultJump;
            this.matchOffsetPairs = matchOffsetPairs;
        }

        @Override
        public String mnemonic() {
            return mnemonic;
        }

        @Override
        public byte value() {
            return value;
        }

        @Override
        public byte[] operands() {
            return operands;
        }

        @Override
        public String show() {
            StringBuilder builder = new StringBuilder("lookupswitch {\n");
            for (int i = 0; i < matchOffsetPairs.length / 2; i++) {
                builder.append('\t').append(matchOffsetPairs[i * 2]).append(": ").append(matchOffsetPairs[i * 2 + 1] + 1).append('\n');
            }
            builder.append("\tdefault").append(": ").append(defaultJump + 1).append("\n}");
            return builder.toString();
        }

        public int getDefaultJump() {
            return defaultJump;
        }

        public int[] getMatchOffsetPairs() {
            return matchOffsetPairs;
        }
    }

    public static class TableSwitchOpcode implements Opcode {
        private final String mnemonic;
        private final byte value;
        private final byte[] operands;
        private final int defaultJump;
        private final int low;
        private final int high;
        private final int[] offsets;

        public TableSwitchOpcode(String mnemonic, byte value, byte[] operands, int defaultJump, int low, int high, int[] offsets) {
            this.mnemonic = mnemonic;
            this.value = value;
            this.operands = operands;
            this.defaultJump = defaultJump;
            this.low = low;
            this.high = high;
            this.offsets = offsets;
        }

        @Override
        public String mnemonic() {
            return mnemonic;
        }

        @Override
        public byte value() {
            return value;
        }

        @Override
        public byte[] operands() {
            return operands;
        }

        @Override
        public String show() {
            StringBuilder builder = new StringBuilder("tableswitch {\n");
            int entry = low;
            for (int offset : offsets) {
                builder.append('\t').append(entry).append(": ").append(offset + 1).append('\n');
                entry++;
            }
            builder.append("\tdefault").append(": ").append(defaultJump + 1).append("\n}");
            return builder.toString();
        }

        public int getDefaultJump() {
            return defaultJump;
        }

        public int getLow() {
            return low;
        }

        public int getHigh() {
            return high;
        }

        public int[] getOffsets() {
            return offsets;
        }
    }

    public static class FixLengthOpcode implements Opcode {
        public static final byte[] EMPTY_OPERANDS = new byte[0];

        private final String mnemonic;
        private final byte value;
        private final byte[] operands;

        public FixLengthOpcode(String mnemonic, byte value, byte[] operands) {
            this.mnemonic = mnemonic;
            this.value = value;
            this.operands = operands;
        }

        public FixLengthOpcode(String mnemonic, byte value) {
            this.mnemonic = mnemonic;
            this.value = value;
            this.operands = EMPTY_OPERANDS;
        }


        public String mnemonic() {
            return mnemonic;
        }

        public byte value() {
            return value;
        }

        public byte[] operands() {
            return operands;
        }

        private String showWide() {
            byte modified = operands[0];

            if ((modified & 0xff) == 0x84) {
                int index = (operands[1] & 0xff) << 8 | (operands[2] & 0xff);
                int cst = (operands[3] & 0xff) << 8 | (operands[4] & 0xff);
                return "wide iinc " + index + " " + cst;
            }

            String opcode;
            if ((modified & 0xff) == 0x15) {
                opcode = "iload";
            } else if ((modified & 0xff) == 0x17) {
                opcode = "fload";
            } else if ((modified & 0xff) == 0x19) {
                opcode = "aload";
            } else if ((modified & 0xff) == 0x16) {
                opcode = "lload";
            } else if ((modified & 0xff) == 0x18) {
                opcode = "dload";
            } else if ((modified & 0xff) == 0x36) {
                opcode = "istore";
            } else if ((modified & 0xff) == 0x38) {
                opcode = "fstore";
            } else if ((modified & 0xff) == 0x3a) {
                opcode = "astore";
            } else if ((modified & 0xff) == 0x37) {
                opcode = "lstore";
            } else if ((modified & 0xff) == 0x39) {
                opcode = "dstore";
            } else if ((modified & 0xff) == 0xa9) {
                opcode = "ret";
            } else {
                throw new ClassFormatError();
            }
            int index = (operands[1] & 0xff) << 8 | (operands[2] & 0xff);
            return "wide " + opcode + " " + index;
        }

        @Override
        public String show() {
            if ((value & 0xff) == 0xc4) {
                return showWide();
            }
            if (operands.length != 0) {
                return showWithOperands();
            }
            return mnemonic;
        }

        private String unsignedOneByte() {
            assert operands.length == 1;
            return Integer.toString(operands[0] & 0xff);
        }

        private String signedOneByte() {
            assert operands.length == 1;
            return Byte.toString(operands[0]);
        }

        private String unsignedTwoBytes() {
            assert operands.length == 2;
            int index = (operands[0] & 0xff) << 8 | (operands[1] & 0xff);
            return Integer.toString(index);
        }

        private String signedTwoBytes() {
            assert operands.length == 2;
            short index = (short) ((operands[0] & 0xff) << 8 | (operands[1] & 0xff));
            return Short.toString(index);
        }

        private String signedFourBytes() {
            assert operands.length == 4;
            int index = byteArrayToInt(operands, 0, 4);
            return Integer.toString(index);
        }

        private String showWithOperands() {
            String append;
            if ((value & 0xff) == 0x19) {
                append = unsignedOneByte();
            } else if ((value & 0xff) == 0xbd) {
                append = unsignedTwoBytes();
            } else if ((value & 0xff) == 0x3a) {
                append = unsignedOneByte();
            } else if ((value & 0xff) == 0x10) {
                append = signedOneByte();
            } else if ((value & 0xff) == 0xc0) {
                append = unsignedTwoBytes();
            } else if ((value & 0xff) == 0x18) {
                append = unsignedOneByte();
            } else if ((value & 0xff) == 0x39) {
                append = unsignedOneByte();
            } else if ((value & 0xff) == 0x17) {
                append = unsignedOneByte();
            } else if ((value & 0xff) == 0x38) {
                append = unsignedOneByte();
            } else if ((value & 0xff) == 0xb4) {
                append = unsignedTwoBytes();
            } else if ((value & 0xff) == 0xb2) {
                append = unsignedTwoBytes();
            } else if ((value & 0xff) == 0xa7) {
                append = unsignedTwoBytes();
            } else if ((value & 0xff) == 0xc8) {
                append = signedFourBytes();
            } else if ((value & 0xff) == 0xa5 || (value & 0xff) == 0xa6 || (value & 0xff) == 0x9f || (value & 0xff) == 0xa0 || (value & 0xff) == 0xa1 || (value & 0xff) == 0xa2 || (value & 0xff) == 0xa3 || (value & 0xff) == 0xa4 || (value & 0xff) == 0x99 || (value & 0xff) == 0x9a || (value & 0xff) == 0x9c || (value & 0xff) == 0x9d || (value & 0xff) == 0x9e || (value & 0xff) == 0xc7 || (value & 0xff) == 0xc6) {
                append = unsignedTwoBytes();
            } else if ((value & 0xff) == 0x84) {
                assert operands.length == 2;
                String index = Integer.toString(operands[0] & 0xff);
                String cst = Byte.toString(operands[1]);
                append = index + " " + cst;
            } else if ((value & 0xff) == 0x15) {
                append = unsignedOneByte();
            } else if ((value & 0xff) == 0xc1) {
                append = unsignedTwoBytes();
            } else if ((value & 0xff) == 0xba) {
                assert operands.length == 4;
                int index = (operands[0] & 0xff) << 8 | (operands[1] & 0xff);
                append = Integer.toString(index);
            } else if ((value & 0xff) == 0xb9) {
                assert operands.length == 4;
                int index = (operands[0] & 0xff) << 8 | (operands[1] & 0xff);
                int cnt = operands[2] & 0xff;
                append = index + " " + cnt;
            } else if ((value & 0xff) == 0xb7) {
                append = unsignedTwoBytes();
            } else if ((value & 0xff) == 0xb8) {
                append = unsignedTwoBytes();
            } else if ((value & 0xff) == 0xb6) {
                append = unsignedTwoBytes();
            } else if ((value & 0xff) == 0x36) {
                append = unsignedOneByte();
            } else if ((value & 0xff) == 0xa8) {
                append = unsignedTwoBytes();
            } else if ((value & 0xff) == 0xc9) {
                append = signedFourBytes();
            } else if ((value & 0xff) == 0x12) {
                append = unsignedOneByte();
            } else if ((value & 0xff) == 0x13) {
                append = unsignedTwoBytes();
            } else if ((value & 0xff) == 0x14) {
                append = unsignedTwoBytes();
            } else if ((value & 0xff) == 0x16) {
                append = unsignedOneByte();
            } else if ((value & 0xff) == 0x37) {
                append = unsignedOneByte();
            } else if ((value & 0xff) == 0xc5) {
                assert operands.length == 3;
                int index = (operands[0] & 0xff) << 8 | (operands[1] & 0xff);
                int dimensions = operands[2] & 0xff;
                append = index + " " + dimensions;
            } else if ((value & 0xff) == 0xbb) {
                append = unsignedTwoBytes();
            } else if ((value & 0xff) == 0xbc) {
                assert operands.length == 1;
                int atype = operands[0];
                if (atype == 4) {
                    append = "Boolean";
                } else if (atype == 5) {
                    append = "Char";
                } else if (atype == 6) {
                    append = "Float";
                } else if (atype == 7) {
                    append = "Double";
                } else if (atype == 8) {
                    append = "Byte";
                } else if (atype == 9) {
                    append = "Short";
                } else if (atype == 10) {
                    append = "Int";
                } else if (atype == 11) {
                    append = "Long";
                } else {
                    throw new ClassFormatError();
                }
            } else if ((value & 0xff) == 0xb5) {
                append = unsignedTwoBytes();

            } else if ((value & 0xff) == 0xb3) {
                append = unsignedTwoBytes();
            } else if ((value & 0xff) == 0xa9) {
                append = unsignedOneByte();
            } else if ((value & 0xff) == 0x11) {
                append = signedTwoBytes();
            } else {
                throw new ClassFormatError();
            }
            return mnemonic + " " + append;
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
        while (pos < codesLen) {
            Opcode opcode = Opcode.parse(values, pos);
            opcodes.add(opcode);
            pos = pos + opcode.operands().length + 1;
        }

        return opcodes;
    }

    public static Code parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        AttributeTable.getAttrLen(input);
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