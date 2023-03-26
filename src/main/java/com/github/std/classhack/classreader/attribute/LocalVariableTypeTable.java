package com.github.std.classhack.classreader.attribute;

import com.github.std.classhack.classreader.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.classhack.classreader.util.BytesReader.readBytes2;

public final class LocalVariableTypeTable implements AttributeInfo {
    private final List<Table> tables;

    public List<Table> getTables() {
        return tables;
    }

    public LocalVariableTypeTable(List<Table> tables) {
        this.tables = tables;
    }

    private static class Table {
        private final int startPc;
        private final int length;
        private final String name;
        private final String signature;
        private final int index;

        public Table(int startPc, int length, String name, String signature, int index) {
            this.startPc = startPc;
            this.length = length;
            this.name = name;
            this.signature = signature;
            this.index = index;
        }

        public int getStartPc() {
            return startPc;
        }

        public int getLength() {
            return length;
        }

        public String getName() {
            return name;
        }

        public String getSignature() {
            return signature;
        }

        public int getIndex() {
            return index;
        }
    }

    public static LocalVariableTypeTable parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        AttributeTable.getAttrLen(input);

        int tbLen = readBytes2(input);
        List<Table> tables = new ArrayList<>(tbLen);
        for (int i = 0; i < tbLen; i++) {
            tables.add(parseTable(input, metaData));
        }
        return new LocalVariableTypeTable(tables);
    }

    private static Table parseTable(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        int startPc = readBytes2(input);
        int length = readBytes2(input);

        int nameIndex = readBytes2(input);
        String name = metaData.constantPool.getUtf8Str(nameIndex);

        int signIndex = readBytes2(input);
        String signature = metaData.constantPool.getUtf8Str(signIndex);

        int index = readBytes2(input);

        return new Table(startPc, length, name, signature, index);
    }
}