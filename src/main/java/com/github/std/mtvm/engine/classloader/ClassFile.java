package com.github.std.mtvm.engine.classloader;


import com.github.std.mtvm.engine.classloader.attribute.AttributeTable;
import com.github.std.mtvm.engine.classloader.constant.ConstantPool;
import com.github.std.mtvm.engine.classloader.field.FieldTable;
import com.github.std.mtvm.engine.classloader.method.MethodTable;

import java.util.List;

public final class ClassFile {
    private final int minorVersion;
    private final int majorVersion;
    private final ConstantPool constantPool;
    private final List<String> accessFlags;
    private final String thisClass;
    private final String superClass;
    private final String[] interfaces;
    private final FieldTable fieldTable;
    private final MethodTable methodTable;
    private final AttributeTable attributeTable;

    public ClassFile(int minorVersion,
                     int majorVersion,
                     ConstantPool constantPool,
                     List<String> accessFlags,
                     String thisClass,
                     String superClass,
                     String[] interfaces,
                     FieldTable fieldTable,
                     MethodTable methodTable,
                     AttributeTable attributeTable) {
        this.minorVersion = minorVersion;
        this.majorVersion = majorVersion;
        this.constantPool = constantPool;
        this.accessFlags = accessFlags;
        this.thisClass = thisClass;
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.fieldTable = fieldTable;
        this.methodTable = methodTable;
        this.attributeTable = attributeTable;
    }

    public static class ClassFileBuilder {
        public int minorVersion;
        public int majorVersion;
        public ConstantPool constantPool;
        public List<String> accessFlags;
        public String thisClass;
        public String superClass;
        public String[] interfaces;
        public FieldTable fieldTable;
        public MethodTable methodTable;
        public AttributeTable attributeTable;

        public ClassFile build() {
            return new ClassFile(
                    minorVersion,
                    majorVersion,
                    constantPool,
                    accessFlags,
                    thisClass,
                    superClass,
                    interfaces,
                    fieldTable,
                    methodTable,
                    attributeTable
            );
        }

        public boolean isInterface() {
            return accessFlags.contains("ACC_INTERFACE");
        }

        public boolean isEnum() {
            return accessFlags.contains("ACC_ENUM");
        }

        public boolean isAnno() {
            return accessFlags.contains("ACC_ANNOTATION");
        }

    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }

    public List<String> getAccessFlags() {
        return accessFlags;
    }

    public String getThisClass() {
        return thisClass;
    }

    public String getSuperClass() {
        return superClass;
    }

    public String[] getInterfaces() {
        return interfaces;
    }

    public FieldTable getFieldInfo() {
        return fieldTable;
    }

    public MethodTable getMethodInfo() {
        return methodTable;
    }

    public AttributeTable getAttributeInfo() {
        return attributeTable;
    }
}
