package com.github.std.classhack.classreader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Class2Test {
    private ClassFile classFile;

    @BeforeEach
    void setUp() throws IOException {
        String file = "/home/maerd_zinbiel/Desktop/CRUD/Lottery/lottery-domain/target/classes/cn/itedus/lottery/domain/strategy/service/algorithm/impl/EntiretyRateRandomDrawAlgorithm.class";
        ClassReader classReader = new ClassReader(Files.newInputStream(Paths.get(file)));
        classFile = classReader.getClassFile();
    }

    @Test
    void test() {

    }
}
