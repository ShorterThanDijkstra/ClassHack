package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtvm.engine.util.BytesReader.readByte;
import static com.github.std.mtvm.engine.util.BytesReader.readBytes2;

public class TypeAnno {
    private final int tag;
    private final TargetInfo targetInfo;
    private final TypePath typePath;
    private final String type;
    private final List<AnnoElementValuePair> pairs;

    public TypeAnno(int tag, TargetInfo targetInfo, TypePath typePath, String type, List<AnnoElementValuePair> pairs) {
        this.tag = tag;
        this.targetInfo = targetInfo;
        this.typePath = typePath;
        this.type = type;
        this.pairs = pairs;
    }

    public int getTag() {
        return tag;
    }

    public TargetInfo getTargetInfo() {
        return targetInfo;
    }

    public TypePath getTypePath() {
        return typePath;
    }

    public String getType() {
        return type;
    }

    public List<AnnoElementValuePair> getPairs() {
        return pairs;
    }

    interface TargetInfo {

    }

    static class TypeParameter implements TargetInfo {
        private final int typeIndex;

        TypeParameter(int index) {
            this.typeIndex = index;
        }

        public int getTypeIndex() {
            return typeIndex;
        }

        public static TypeParameter parse(InputStream input) throws IOException {
            int index = readByte(input);
            return new TypeParameter(index);
        }
    }

    static class Supertype implements TargetInfo {
        private final int supertypeIndex;

        Supertype(int supertypeIndex) {
            this.supertypeIndex = supertypeIndex;
        }

        public int getSupertypeIndex() {
            return supertypeIndex;
        }

        public static Supertype parse(InputStream input) throws IOException {
            int index = readBytes2(input);
            return new Supertype(index);
        }
    }

    static class TypeParameterBound implements TargetInfo {
        private final int typeParaIndex;
        private final int boundIndex;

        public TypeParameterBound(int typeParaIndex, int boundIndex) {
            this.typeParaIndex = typeParaIndex;
            this.boundIndex = boundIndex;
        }

        public int getTypeParaIndex() {
            return typeParaIndex;
        }

        public int getBoundIndex() {
            return boundIndex;
        }

        public static TypeParameterBound parse(InputStream input) throws IOException {
            int typeParaIndex = readByte(input);
            int boundIndex = readByte(input);
            return new TypeParameterBound(typeParaIndex, boundIndex);
        }
    }

    static class Empty implements TargetInfo {

    }

    static class FormalParameter implements TargetInfo {
        private final int formalParaIndex;

        FormalParameter(int formalParaIndex) {
            this.formalParaIndex = formalParaIndex;
        }

        public int getFormalParaIndex() {
            return formalParaIndex;
        }

        public static FormalParameter parse(InputStream input) throws IOException {
            int index = readByte(input);
            return new FormalParameter(index);
        }
    }

    static class Throws implements TargetInfo {
        private final int throwsTypeIndex;

        public Throws(int throwsTypeIndex) {
            this.throwsTypeIndex = throwsTypeIndex;
        }

        public int getThrowsTypeIndex() {
            return throwsTypeIndex;
        }

        public static Throws parse(InputStream input) throws IOException {
            int index = readBytes2(input);
            return new Throws(index);
        }
    }

    static class LocalVar implements TargetInfo {
        private final List<Entry> table;

        LocalVar(List<Entry> table) {
            this.table = table;
        }

        public List<Entry> getTable() {
            return table;
        }

        private static class Entry {
            private final int startPc;
            private final int length;
            private final int index;

            public Entry(int startPc, int length, int index) {
                this.startPc = startPc;
                this.length = length;
                this.index = index;
            }

            public int getStartPc() {
                return startPc;
            }

            public int getLength() {
                return length;
            }

            public int getIndex() {
                return index;
            }

            static Entry parse(InputStream input) throws IOException {
                int startPc = readBytes2(input);
                int length = readBytes2(input);
                int index = readBytes2(input);
                return new Entry(startPc, length, index);
            }
        }

        public static LocalVar parse(InputStream input) throws IOException {
            int len = readBytes2(input);
            List<Entry> table = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                table.add(Entry.parse(input));
            }
            return new LocalVar(table);
        }
    }

    static class Catch implements TargetInfo {
        private final int exceptionTableIndex;

        public Catch(int exceptionTableIndex) {
            this.exceptionTableIndex = exceptionTableIndex;
        }

        public int getExceptionTableIndex() {
            return exceptionTableIndex;
        }

        public static Catch parse(InputStream input) throws IOException {
            int index = readBytes2(input);
            return new Catch(index);
        }
    }

    static class Offset implements TargetInfo {
        private final int offset;

        public Offset(int offset) {
            this.offset = offset;
        }

        public int getOffset() {
            return offset;
        }

        public static Offset parse(InputStream input) throws IOException {
            int offset = readBytes2(input);
            return new Offset(offset);
        }
    }

    static class TypeArgument implements TargetInfo {
        private final int offset;
        private final int typeArgIndex;

        public TypeArgument(int offset, int typeArgIndex) {
            this.offset = offset;
            this.typeArgIndex = typeArgIndex;
        }

        public int getOffset() {
            return offset;
        }

        public int getTypeArgIndex() {
            return typeArgIndex;
        }

        public static TypeArgument parse(InputStream input) throws IOException {
            int offset = readBytes2(input);
            int typeArgIndex = readByte(input);
            return new TypeArgument(offset, typeArgIndex);
        }
    }

    static class TypePath {
        private final List<Path> paths;

        TypePath(List<Path> paths) {
            this.paths = paths;
        }

        public List<Path> getPaths() {
            return paths;
        }

        static TypePath parse(InputStream input) throws IOException {
            int len = readByte(input);
            List<Path> paths = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                paths.add(Path.parse(input));
            }
            return new TypePath(paths);
        }

        private static class Path {
            private final int typePathKind;
            private final int typeArgIndex;

            public Path(int typePathKind, int typeArgIndex) {
                this.typePathKind = typePathKind;
                this.typeArgIndex = typeArgIndex;
            }

            public int getTypePathKind() {
                return typePathKind;
            }

            public int getTypeArgIndex() {
                return typeArgIndex;
            }

            static Path parse(InputStream input) throws IOException {
                int kind = readByte(input);
                int index = readByte(input);
                return new Path(kind, index);
            }
        }
    }

    public static TypeAnno parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        final int tag = readByte(input);

        final TargetInfo targetInfo = parseTargetInfo(input, tag);

        final TypePath typePath = TypePath.parse(input);

        int typeIndex = readBytes2(input);
        final String type = metaData.constantPool.getUtf8Str(typeIndex);

        int pairsNum = readBytes2(input);
        final List<AnnoElementValuePair> pairs = new ArrayList<>(pairsNum);
        for (int i = 0; i < pairsNum; i++) {
            pairs.add(AnnoElementValuePair.parse(input, metaData));
        }

        return new TypeAnno(tag, targetInfo, typePath, type, pairs);
    }

    private static TargetInfo parseTargetInfo(InputStream input, int tag) throws IOException {
        if (tag == 0x00 || tag == 0x01) {
            return TypeParameter.parse(input);
        }
        if (tag == 0x10) {
            return Supertype.parse(input);
        }
        if (tag == 0x11 || tag == 0x12) {
            return TypeParameterBound.parse(input);
        }
        if (tag == 0x13 || tag == 0x14 || tag == 0x15) {
            return new Empty();
        }
        if (tag == 0x16) {
            return FormalParameter.parse(input);
        }
        if (tag == 0x17) {
            return Throws.parse(input);
        }
        if (tag == 0x40 || tag == 0x41) {
            return LocalVar.parse(input);
        }
        if (tag == 0x42) {
            return Catch.parse(input);
        }
        if (tag == 0x43 || tag == 0x44 || tag == 0x45 || tag == 0x46) {
            return Offset.parse(input);
        }
        if (tag == 0x47 || tag == 0x48 || tag == 0x49 || tag == 0x4a || tag == 0x4b) {
            return TypeArgument.parse(input);
        }
        throw new ClassFormatError();
    }
}
