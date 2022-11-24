package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtvm.engine.classloader.attribute.AttributeTable.getAttrLen;
import static com.github.std.mtvm.engine.util.BytesReader.readBytes2;

public final class RuntimeInvisibleAnnotations implements AttributeInfo {
    private final List<Anno> annos;

    public RuntimeInvisibleAnnotations(List<Anno> annos) {
        this.annos = annos;
    }

    public List<Anno> getAnnos() {
        return annos;
    }

    public static RuntimeInvisibleAnnotations parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        // todo: test
        getAttrLen(input);

        int annoNum = readBytes2(input);
        List<Anno> annos = new ArrayList<>(annoNum);
        for (int i = 0; i < annoNum; i++) {
            annos.add(Anno.parse(input, metaData));
        }
        return new RuntimeInvisibleAnnotations(annos);
    }
}