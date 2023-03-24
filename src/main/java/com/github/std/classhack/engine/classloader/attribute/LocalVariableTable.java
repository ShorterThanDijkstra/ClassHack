package com.github.std.classhack.engine.classloader.attribute;

import com.github.std.classhack.engine.classloader.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.classhack.engine.classloader.attribute.AttributeChecker.checkLocalVariableTableDescIndex;
import static com.github.std.classhack.engine.classloader.attribute.AttributeChecker.checkLocalVariableTableNameIndex;
import static com.github.std.classhack.engine.classloader.attribute.AttributeTable.getAttrLen;
import static com.github.std.classhack.engine.util.BytesReader.readBytes2;

public final class LocalVariableTable implements AttributeInfo {
    private final List<Table> tables;

    public LocalVariableTable(List<Table> tables) {
        this.tables = tables;
    }

    private static class Table {
        private final int startPc;
        private final int length;
        private final String name;
        private final String desc;
        private final int index;

        public Table(int startPc, int length, String name, String desc, int index) {
            this.startPc = startPc;
            this.length = length;
            this.name = name;
            this.desc = desc;
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

        public String getDesc() {
            return desc;
        }

        public int getIndex() {
            return index;
        }
    }

    public static LocalVariableTable parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        getAttrLen(input);

        int tableLen = readBytes2(input);
        List<Table> tables = new ArrayList<>(tableLen);
        for (int i = 0; i < tableLen; i++) {
            tables.add(parseTables(input, metaData));
        }
        return new LocalVariableTable(tables);
    }

    private static Table parseTables(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        int startPc = readBytes2(input);
        int length = readBytes2(input);

        int nameIndex = readBytes2(input);
        String name = checkLocalVariableTableNameIndex(nameIndex, metaData.constantPool);

        int descIndex = readBytes2(input);
        String desc = checkLocalVariableTableDescIndex(descIndex, metaData.constantPool);

        int index = readBytes2(input);
        return new Table(startPc, length, name, desc, index);
    }

    public List<Table> getTables() {
        return tables;
    }
}