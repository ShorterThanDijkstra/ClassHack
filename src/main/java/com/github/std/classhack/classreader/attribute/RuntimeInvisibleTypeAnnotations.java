package com.github.std.classhack.classreader.attribute;

import com.github.std.classhack.classreader.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.classhack.classreader.util.BytesReader.readBytes2;

public final class RuntimeInvisibleTypeAnnotations implements AttributeInfo {
    private final List<TypeAnno> typeAnnos;

    public RuntimeInvisibleTypeAnnotations(List<TypeAnno> typeAnnos) {
        this.typeAnnos = typeAnnos;
    }

    public List<TypeAnno> getTypeAnnos() {
        return typeAnnos;
    }

    public static RuntimeInvisibleTypeAnnotations parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long len = AttributeTable.getAttrLen(input);

        int annoNum = readBytes2(input);
        List<TypeAnno> typeAnnos = new ArrayList<>(annoNum);
        for (int i = 0; i < annoNum; i++) {
            typeAnnos.add(TypeAnno.parse(input, metaData));
        }
        return new RuntimeInvisibleTypeAnnotations(typeAnnos);
    }
}