package com.github.std.classhack.classreader.attribute;

import com.github.std.classhack.classreader.constant.*;

import java.util.List;

public class AttributeChecker {
    public static Constant checkConstantValue(int index, ConstantPool constantPool) {
        Constant constant = constantPool.getPool().get(index - 1);
        if (!(constant instanceof ConstantLong ||
                constant instanceof ConstantFloat ||
                constant instanceof ConstantDouble ||
                constant instanceof ConstantInteger ||
                constant instanceof ConstantString)) {
            throw new ClassFormatError();
        }
        return constant;
    }


    public static String checkExceptions(int index, ConstantPool constantPool) {
        return constantPool.getConstClassName(index);
    }

    public static String checkInnerClassInnerInfoIndex(int index, ConstantPool constantPool) {
        return constantPool.getConstClassName(index);
    }

    public static String checkInnerClassOuterInfoIndex(int index, ConstantPool constantPool) {
        if (index == 0) {
            return "";
        }
        return constantPool.getConstClassName(index);
    }

    public static String checkInnerClassInnerName(int index, ConstantPool constantPool) {
        if (index == 0) {
            return "";
        }
        Constant constUtf8 = constantPool.getPool().get(index - 1);
        if (!(constUtf8 instanceof ConstantUtf8)) {
            throw new ClassFormatError();
        }
        return ((ConstantUtf8) constUtf8).getValue();
    }

    public static String checkEnclosingMethodClassIndex(int index, ConstantPool constantPool) {
        return constantPool.getConstClassName(index);
    }

    public static String checkEnclosingMethodMethodIndex(int index, ConstantPool constantPool) {
        if (index == 0) {
            return "";
        }
        List<Constant> constants = constantPool.getPool();
        Constant nameAndType = constants.get(index - 1);
        if (!(nameAndType instanceof ConstantNameAndType)) {
            throw new ClassFormatError();
        }
        int nameIndex = ((ConstantNameAndType) nameAndType).getNameIndex();
        int descIndex = ((ConstantNameAndType) nameAndType).getDescriptorIndex();
        Constant constUtf8 = constants.get(nameIndex - 1);
        if (!(constUtf8 instanceof ConstantUtf8)) {
            throw new ClassFormatError();
        }
        String name = ((ConstantUtf8) constUtf8).getValue();

        constUtf8 = constants.get(descIndex - 1);
        if (!(constUtf8 instanceof ConstantUtf8)) {
            throw new ClassFormatError();
        }
        String desc = ((ConstantUtf8) constUtf8).getValue();
        return desc + " " + name;
    }

    public static String checkSignature(int index, ConstantPool constantPool) {
        return constantPool.getUtf8Str(index);
    }

    public static String checkSourceFile(int index, ConstantPool constantPool) {
        return constantPool.getUtf8Str(index);
    }

    public static String checkLocalVariableTableNameIndex(int index, ConstantPool constantPool) {
        return constantPool.getUtf8Str(index);
    }

    public static String checkLocalVariableTableDescIndex(int index, ConstantPool constantPool) {
        return constantPool.getUtf8Str(index);
    }

    public static void checkConstElementValueTag(int tag, Constant constant) {
        if ((tag == 'B' || tag == 'C' || tag == 'I' || tag == 'S' || tag == 'Z')
                && !(constant instanceof ConstantInteger)) {
            throw new ClassFormatError();
        }
        if (tag == 'D' && !(constant instanceof ConstantDouble)) {
            throw new ClassFormatError();
        }
        if (tag == 'F' && !(constant instanceof ConstantFloat)) {
            throw new ClassFormatError();
        }
        if (tag == 'J' && !(constant instanceof ConstantLong)) {
            throw new ClassFormatError();
        }
        if (tag == 's' && !(constant instanceof ConstantUtf8)) {
            throw new ClassFormatError();
        }
    }

    public static ConstantMethodHandle checkBootstrapMethodsMethodRef(int index, ConstantPool constantPool) {
        Constant constant = constantPool.get(index - 1);
        if (!(constant instanceof ConstantMethodHandle)) {
            throw new ClassFormatError();
        }
        return (ConstantMethodHandle) constant;
    }

    public static Constant checkBootstrapMethodsArgument(int index, ConstantPool constantPool) {
        Constant constant = constantPool.get(index - 1);
        if (!(constant instanceof ConstantString ||
                constant instanceof ConstantClass ||
                constant instanceof ConstantInteger ||
                constant instanceof ConstantLong ||
                constant instanceof ConstantFloat ||
                constant instanceof ConstantDouble ||
                constant instanceof ConstantMethodHandle ||
                constant instanceof ConstantMethodType)) {
            throw new ClassFormatError();
        }
        return constant;
    }

}
