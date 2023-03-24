package com.github.std.classhack.engine.classloader.attribute;

import com.github.std.classhack.engine.classloader.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.classhack.engine.classloader.attribute.AttributeTable.getAttrLen;
import static com.github.std.classhack.engine.util.BytesReader.readBytes2;

public final class MethodParameters implements AttributeInfo {
    private final List<Parameter> parameters;

    public MethodParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    private final static class Parameter {
        private final String name;
        private final List<String> accessFlags;

        public static Parameter parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
            int nameIndex = readBytes2(input);
            final String name;
            if (nameIndex == 0) {
                name = "";
            } else {
                name = metaData.constantPool.getUtf8Str(nameIndex);
            }

            byte[] bsAccessFlags = new byte[2];
            int read = input.read(bsAccessFlags);
            assert read == 2;
            final List<String> accessFlags = new ArrayList<>();
            if ((bsAccessFlags[1] & 0x10) == 0x10) {
                accessFlags.add("ACC_FINAL");
            }
            if ((bsAccessFlags[0] & 0x10) == 0x10) {
                accessFlags.add("ACC_SYNTHETIC");
            }
            if ((bsAccessFlags[0] & 0x80) == 0x80) {
                accessFlags.add("ACC_MANDATED");
            }

            return new Parameter(name, accessFlags);
        }

        public String getName() {
            return name;
        }

        public List<String> getAccessFlags() {
            return accessFlags;
        }

        public Parameter(String name, List<String> accessFlags) {
            this.name = name;
            this.accessFlags = accessFlags;
        }
    }

    public static MethodParameters parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long len = getAttrLen(input);

        int paraCount = readBytes2(input);
        List<Parameter> parameters = new ArrayList<>(paraCount);
        for (int i = 0; i < paraCount; i++) {
            parameters.add(Parameter.parse(input, metaData));
        }
        return new MethodParameters(parameters);
    }
}