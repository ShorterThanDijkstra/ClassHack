package com.github.std.classhack.classreader.attribute;

import com.github.std.classhack.classreader.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.classhack.classreader.attribute.AttributeTable.getAttrLen;
import static com.github.std.classhack.classreader.util.BytesReader.readByte;
import static com.github.std.classhack.classreader.util.BytesReader.readBytes2;

public final class RuntimeInvisibleParameterAnnotations implements AttributeInfo {
    private final List<InvisibleParaAnno> paraAnnos;

    public RuntimeInvisibleParameterAnnotations(List<InvisibleParaAnno> paraAnnos) {
        this.paraAnnos = paraAnnos;
    }

    public List<InvisibleParaAnno> getParaAnnos() {
        return paraAnnos;
    }

    private final static class InvisibleParaAnno {
        private final List<Anno> annos;

        private InvisibleParaAnno(List<Anno> annos) {
            this.annos = annos;
        }

        public static InvisibleParaAnno parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
            int annoNum = readBytes2(input);
            List<Anno> annos = new ArrayList<>(annoNum);
            for (int i = 0; i < annoNum; i++) {
                annos.add(Anno.parse(input, metaData));
            }
            return new InvisibleParaAnno(annos);
        }

        public List<Anno> getAnnos() {
            return annos;
        }
    }

    public static RuntimeInvisibleParameterAnnotations parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        getAttrLen(input);

        int paraNum = readByte(input);
        List<InvisibleParaAnno> paraAnnos = new ArrayList<>(paraNum);
        for (int i = 0; i < paraNum; i++) {
            paraAnnos.add(InvisibleParaAnno.parse(input, metaData));
        }
        return new RuntimeInvisibleParameterAnnotations(paraAnnos);
    }
}