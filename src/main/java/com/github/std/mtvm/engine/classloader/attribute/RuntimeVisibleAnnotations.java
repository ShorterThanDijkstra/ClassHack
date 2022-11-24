package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtvm.engine.classloader.attribute.AttributeTable.getAttrLen;
import static com.github.std.mtvm.engine.util.BytesReader.readBytes2;

public final class RuntimeVisibleAnnotations implements AttributeInfo {
    private final List<Anno> annos;

    public List<Anno> getAnnos() {
        return annos;
    }

    public RuntimeVisibleAnnotations(List<Anno> annos) {
        this.annos = annos;
    }

    public static RuntimeVisibleAnnotations parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long len = getAttrLen(input);

        int annoNum = readBytes2(input);
        List<Anno> annos = new ArrayList<>(annoNum);
        for (int i = 0; i < annoNum; i++) {
            annos.add(Anno.parse(input, metaData));
        }
        return new RuntimeVisibleAnnotations(annos);
    }
}