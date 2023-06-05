package com.github.std.classhack.dev;

import com.github.std.classhack.classreader.attribute.Code;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Modifier;
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
      generate codes for method: parseFixedLengthOpcode(final byte[] values, final byte value, final int pos)
      in com.github.std.classhack.classreader.attribute.Code.Opcode,
      which parses bytecode array.
       lookupswitch, tableswitch, wide are special
     */
    public void genOpcodeParser() {
        final String OPCODES_DEFINE_FILE = "src/main/java/com/github/std/classhack/dev/opcodes.txt";
        final String METHOD_CODE_FILE = "src/main/java/com/github/std/classhack/dev/opcode_parse.txt";

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("parseFixedLengthOpcode")
                .addModifiers(Modifier.STATIC)
                .returns(Code.Opcode.class)
                .addParameter(byte[].class, "values", Modifier.FINAL)
                .addParameter(byte.class, "value", Modifier.FINAL)
                .addParameter(int.class, "pos", Modifier.FINAL);

        try (LineNumberReader reader = new LineNumberReader(new FileReader(OPCODES_DEFINE_FILE));
             FileWriter writer = new FileWriter(METHOD_CODE_FILE)) {

            String line = reader.readLine();
            while (line != null) {
                String[] info = line.split("\\s+");
                if (info.length != 3) {
                    throw new RuntimeException();
                }

                String mnemonic = info[0];
                String value = info[1];
                int after = Integer.parseInt(info[2]);
                if (after == 0) {
                    methodBuilder.beginControlFlow(
                                    "if((value & 0xff) == $L)", value)
                            .addStatement("return new FixLengthOpcode($S, value, pos)", mnemonic)
                            .endControlFlow();
                } else {
                    StringBuilder operandsBuilder = new StringBuilder();
                    for (int i = 1; i <= after; i++) {
                        operandsBuilder.append("values[pos + ").append(i).append("]");
                        if (i != after) {
                            operandsBuilder.append(", ");
                        }
                    }
                    methodBuilder.beginControlFlow("if((value & 0xff) == $L)", value)
                            .addStatement("return new FixLengthOpcode($S, value, $L)", mnemonic, operandsBuilder.toString())
                            .endControlFlow();
                }
                line = reader.readLine();
            }
            MethodSpec method = methodBuilder.build();

            writer.write(method.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void genOpcodeParser_() {
        final String OPCODES_DEFINE_FILE = "src/main/java/com/github/std/classhack/dev/opcodes.txt";
        final String METHOD_CODE_FILE = "src/main/java/com/github/std/classhack/dev/opcode_parse.txt";

        try (LineNumberReader reader = new LineNumberReader(new FileReader(OPCODES_DEFINE_FILE));
             FileWriter writer = new FileWriter(METHOD_CODE_FILE)) {
            writer.write("{\n");
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
                    writer.write("        return new FixLengthOpcode(\"" + mnemonic + "\", value, pos);\n");
                } else {
                    operandsBuilder.append("new byte[]{");
                    for (int i = 1; i <= after; i++) {
                        operandsBuilder.append("values[pos + ").append(i).append("]");
                        if (i != after) {
                            operandsBuilder.append(", ");
                        }
                    }
                    operandsBuilder.append("}");
                    writer.write("        return new FixLengthOpcode(\"" + mnemonic + "\", value, " + operandsBuilder + ",pos);\n");
                    operandsBuilder.setLength(0);
                }

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
