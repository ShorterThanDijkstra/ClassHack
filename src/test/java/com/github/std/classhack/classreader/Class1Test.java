package com.github.std.classhack.classreader;

import com.github.std.classhack.classreader.attribute.AttributeInfo;
import com.github.std.classhack.classreader.attribute.Code;
import com.github.std.classhack.classreader.method.MethodInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Class1Test {
    private ClassFile classFile;

    @BeforeEach
    void setUp() throws IOException {
        String file = "target/test-classes/com/github/std/classhack/classreader/Class1.class";
        ClassReader classReader = new ClassReader(Files.newInputStream(Paths.get(file)));
        classFile = classReader.getClassFile();
    }

    private void showOpcodes(Code code) {
        List<Code.Opcode> opcodes = code.getOpcodes();
        for (Code.Opcode opcode : opcodes) {
            String mnemonic = opcode.mnemonic();
            byte[] operands = opcode.operands();
            System.out.print(mnemonic);
            System.out.print("\t");
            for (byte b : operands) {
                System.out.print(Integer.toHexString(Byte.toUnsignedInt(b)));
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    @Test
    void opcodeTest0() {
        MethodInfo methodInfo = classFile.getMethodTable().getMethodInfos().get(1);

        String name = methodInfo.getName();
        assertEquals("test0", name);

        List<AttributeInfo> attributes = methodInfo.getAttributeTable().getAttributes();
        Code code = (Code) attributes.get(0);

        showOpcodes(code);
    }

    @Test
    void opcodeTest1() {
        MethodInfo methodInfo = classFile.getMethodTable().getMethodInfos().get(2);
        String name = methodInfo.getName();
        assertEquals("test1", name);

        List<AttributeInfo> attributes = methodInfo.getAttributeTable().getAttributes();
        Code code = (Code) attributes.get(0);

        showOpcodes(code);

    }
}
