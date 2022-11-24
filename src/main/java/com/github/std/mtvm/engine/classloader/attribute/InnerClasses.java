package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtvm.engine.classloader.attribute.AttributeChecker.*;
import static com.github.std.mtvm.engine.classloader.attribute.AttributeTable.getAttrLen;
import static com.github.std.mtvm.engine.util.BytesReader.readBytes2;

public final class InnerClasses implements AttributeInfo {
    public List<ClassInfo> getClassInfos() {
        return classInfos;
    }

    private final List<ClassInfo> classInfos;

    private InnerClasses(List<ClassInfo> classInfos) {
        this.classInfos = classInfos;
    }

    private static class ClassInfo {
        private final String innerClassName;
        private final String outerClassName;
        private final String className;
        private final List<String> accessFlags;

        private ClassInfo(String innerClassName,
                          String outerClassName,
                          String className,
                          List<String> accessFlags) {
            this.innerClassName = innerClassName;
            this.outerClassName = outerClassName;
            this.className = className;
            this.accessFlags = accessFlags;
        }

        public String getInnerClassName() {
            return innerClassName;
        }

        public String getOuterClassName() {
            return outerClassName;
        }

        public String getClassName() {
            return className;
        }

        public List<String> getAccessFlags() {
            return accessFlags;
        }
    }

    public static InnerClasses parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        getAttrLen(input);
        int classNum = readBytes2(input);
        List<ClassInfo> classInfos = new ArrayList<>(classNum);
        for (int i = 0; i < classNum; i++) {
            classInfos.add(parseClass(input, metaData));
        }
        return new InnerClasses(classInfos);
    }

    private static ClassInfo parseClass(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        int innerClassIndex = readBytes2(input);
        String innerClassName = checkInnerClassInnerInfoIndex(innerClassIndex, metaData.constantPool);

        int outerClassIndex = readBytes2(input);
        String outerClassName = checkInnerClassOuterInfoIndex(outerClassIndex, metaData.constantPool);

        int classNameIndex = readBytes2(input);
        String className = checkInnerClassInnerName(classNameIndex, metaData.constantPool);

        byte[] bsAccessFlags = new byte[2];
        int read = input.read(bsAccessFlags);
        assert read == 2;
        List<String> accessFlags = new ArrayList<>();
        if ((bsAccessFlags[1] & 0x01) == 0x01) {
            accessFlags.add("ACC_PUBLIC");
        }
        if ((bsAccessFlags[1] & 0x02) == 0x02) {
            accessFlags.add("ACC_PRIVATE");
        }
        if ((bsAccessFlags[1] & 0x04) == 0x04) {
            accessFlags.add("ACC_PROTECTED");
        }
        if ((bsAccessFlags[1] & 0x08) == 0x08) {
            accessFlags.add("ACC_STATIC");
        }
        if ((bsAccessFlags[1] & 0x10) == 0x10) {
            accessFlags.add("ACC_FINAL");
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
        return new ClassInfo(innerClassName, outerClassName, className, accessFlags);
    }
}