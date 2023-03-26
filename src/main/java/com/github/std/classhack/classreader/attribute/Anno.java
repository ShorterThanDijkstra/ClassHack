package com.github.std.classhack.classreader.attribute;

import com.github.std.classhack.classreader.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.classhack.classreader.util.BytesReader.readBytes2;

public class Anno {
    private final String type;
    private final List<AnnoElementValuePair> pairs;


    public String getType() {
        return type;
    }

    public List<AnnoElementValuePair> getPairs() {
        return pairs;
    }

    public Anno(String type, List<AnnoElementValuePair> pairs) {
        this.type = type;
        this.pairs = pairs;
    }


    public static Anno parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        int typeIndex = readBytes2(input);
        String type = metaData.constantPool.getUtf8Str(typeIndex);

        int pairNum = readBytes2(input);
        List<AnnoElementValuePair> pairs = new ArrayList<>(pairNum);
        for (int i = 0; i < pairNum; i++) {
            pairs.add(AnnoElementValuePair.parse(input, metaData));
        }
        return new Anno(type, pairs);
    }
}



