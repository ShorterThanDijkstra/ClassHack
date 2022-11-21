package com.github.std.mtvm.dev;

import java.io.FileWriter;
import java.io.IOException;

public class CodeGen {

    public void genClassesForInterface(String[] classNames,
                                       String interfaceName,
                                       String packageName,
                                       String sourceDir) throws IOException {
        for (String className : classNames) {
            try (FileWriter writer = new FileWriter(sourceDir + className + ".java")) {
                writer.write("package " + packageName + ";\n\n");
                writer.write("public final class ");
                writer.write(className);
                writer.write(" implements ");
                writer.write(interfaceName);
                writer.write(" {\n\n");
                writer.write("}");
            }
        }

    }

}
