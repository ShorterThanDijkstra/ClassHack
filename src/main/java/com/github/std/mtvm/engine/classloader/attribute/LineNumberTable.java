package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtvm.engine.classloader.attribute.AttributeTable.getAttrLen;
import static com.github.std.mtvm.engine.util.BytesReader.readBytes2;

public final class LineNumberTable implements AttributeInfo {
    private final List<Table> tables;

    private LineNumberTable(List<Table> tables) {
        this.tables = tables;
    }

    public List<Table> getTables() {
        return tables;
    }

    private static class Table {
        private final int startPc;
        private final int lineNum;

        public Table(int startPc, int lineNum) {
            this.startPc = startPc;
            this.lineNum = lineNum;
        }

        public int getStartPc() {
            return startPc;
        }

        public int getLineNum() {
            return lineNum;
        }
    }

    public static LineNumberTable parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        getAttrLen(input);

        int tableLen = readBytes2(input);
        List<Table> tables = new ArrayList<>(tableLen);
        for (int i = 0; i < tableLen; i++) {
            int startPc = readBytes2(input);
            int lineNum = readBytes2(input);
            tables.add(new Table(startPc, lineNum));
        }
        return new LineNumberTable(tables);
    }
}