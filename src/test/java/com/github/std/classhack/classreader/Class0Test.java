package com.github.std.classhack.classreader;

import com.github.std.classhack.classreader.constant.Constant;
import com.github.std.classhack.classreader.constant.ConstantPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Class0Test {
    private ClassFile classFile;

    @BeforeEach
    void setUp() throws IOException {
        String file = "target/test-classes/com/github/std/classhack/classreader/Class0.class";
        ClassReader classReader = new ClassReader(Files.newInputStream(Paths.get(file)));
        classFile = classReader.getClassFile();
    }

    @Test
    void version() {
        int minorVersion = classFile.getMinorVersion();
        int majorVersion = classFile.getMajorVersion();
        assertEquals(0, minorVersion);
        assertEquals(52, majorVersion);
    }

    @Test
    void constantPool() {
        ConstantPool constantPool = classFile.getConstantPool();
        List<Constant> pool = constantPool.getPool();
    }

    @Test
    void accessFlag() {
        List<String> accessFlags = classFile.getAccessFlags();
        System.out.println(accessFlags);
    }

    @Test
    void thisClass() {
        String thisClass = classFile.getThisClass();
        assertEquals("com/github/std/classhack/classreader/Class0", thisClass);
    }

    @Test
    void superClass() {
        String superClass = classFile.getSuperClass();
        assertEquals("java/lang/Object", superClass);
    }

    @Test
    void interfaces() {
        String[] interfaces = classFile.getInterfaces();
        System.out.println(Arrays.toString(interfaces));
    }
}