package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtvm.engine.classloader.attribute.AttributeTable.getAttrLen;
import static com.github.std.mtvm.engine.util.BytesReader.readByte;
import static com.github.std.mtvm.engine.util.BytesReader.readBytes2;

public final class RuntimeVisibleParameterAnnotations implements AttributeInfo {
    private final List<VisibleParaAnno> visibleParaAnnos;

    private final static class VisibleParaAnno {
        private final List<Anno> annos;

        private VisibleParaAnno(List<Anno> annos) {
            this.annos = annos;
        }

        public static VisibleParaAnno parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
            int annoNum = readBytes2(input);
            List<Anno> annos = new ArrayList<>(annoNum);
            for (int i = 0; i < annoNum; i++) {
                annos.add(Anno.parse(input, metaData));
            }
            return new VisibleParaAnno(annos);
        }

        public List<Anno> getAnnos() {
            return annos;
        }
    }

    public RuntimeVisibleParameterAnnotations(List<VisibleParaAnno> visibleParaAnnos) {
        this.visibleParaAnnos = visibleParaAnnos;
    }

    public List<VisibleParaAnno> getParaAnnos() {
        return visibleParaAnnos;
    }

    public static RuntimeVisibleParameterAnnotations parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        getAttrLen(input);

        int paraNum = readByte(input);
        List<VisibleParaAnno> visibleParaAnnos = new ArrayList<>(paraNum);
        for (int i = 0; i < paraNum; i++) {
            visibleParaAnnos.add(VisibleParaAnno.parse(input, metaData));
        }
        return new RuntimeVisibleParameterAnnotations(visibleParaAnnos);
    }
}