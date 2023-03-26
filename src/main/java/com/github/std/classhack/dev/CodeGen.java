package com.github.std.classhack.dev;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;

public class CodeGen {

    public void genClassesForInterface(String[] classNames, String interfaceName, String packageName, String sourceDir) throws IOException {
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

    /*
      generate codes for com.github.std.classhack.engine.classloader.attribute.Code.Opcode.parse(byte[] values, int pos)
      which parses bytecode array.
      exception: lookupswitch, tableswitch, wide
     */
    public void genOpcodeParser() {
        final String OPCODES_DEFINE_FILE = "src/main/java/com/github/std/classhack/dev/opcodes.txt";
        final String METHOD_CODE_FILE = "src/main/java/com/github/std/classhack/dev/opcode_parse.txt";
        try (LineNumberReader reader = new LineNumberReader(new FileReader(OPCODES_DEFINE_FILE));
             FileWriter writer = new FileWriter(METHOD_CODE_FILE)) {
            writer.write("public static Opcode parse(byte[] values, int pos) {\n");
            writer.write("    byte value = values[pos];\n");

            String line = reader.readLine();
            StringBuilder operandsBuilder = new StringBuilder();
            while (line != null) {
                String[] info = line.split("\\s+");
                if (info.length != 3) {
                    throw new RuntimeException();
                }

                String mnemonic = info[0];
                String value = info[1];
                int after = Integer.parseInt(info[2]);

                writer.write("    if((value & 0xff) == " + value + ") {\n");
                if (after == 0) {
                    writer.write("        return new FixLengthOpcode(\"" + mnemonic + "\", value);\n");
                } else {
                    operandsBuilder.append("new byte[]{");
                    for (int i = 1; i <= after; i++) {
                        operandsBuilder.append("values[pos + ").append(i).append("]");
                        if (i != after) {
                            operandsBuilder.append(", ");
                        }
                    }
                    operandsBuilder.append("}");
                    writer.write("        return new FixLengthOpcode(\"" + mnemonic + "\", value, " + operandsBuilder + ");\n");
                    operandsBuilder.setLength(0);
                }

                writer.write("    }\n");

                line = reader.readLine();
            }

            writer.write("    throw new ClassFormatError(\"Unknown Opcode: \" + Integer.toHexString(value & 0xff));\n");
            writer.write("}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        CodeGen codeGen = new CodeGen();
        codeGen.genOpcodeParser();
    }
}
