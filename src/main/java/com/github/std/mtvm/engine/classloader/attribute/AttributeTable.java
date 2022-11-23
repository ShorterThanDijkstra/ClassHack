package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.ClassFile;
import com.github.std.mtvm.engine.classloader.constant.ConstantPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtvm.engine.util.BytesReader.readBytes2;
import static com.github.std.mtvm.engine.util.BytesReader.readBytes4;
import static com.github.std.mtvm.engine.util.Logger.info;

public class AttributeTable {
    private final List<AttributeInfo> attributes;

    public static long getAttrLen(InputStream input) throws IOException {
        return readBytes4(input);
    }

    public AttributeTable(int attrCount, InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        attributes = new ArrayList<>(attrCount);
        for (int i = 0; i < attrCount; i++) {
            parseAttr(input, metaData);
        }
    }

    private void parseAttr(InputStream input,
                           ClassFile.ClassFileBuilder metaData) throws IOException {
        String name = getAttrName(input, metaData.constantPool);
        if ("ConstantValue".equals(name)) {
            attributes.add(new ConstantValue(input, metaData));
        } else if ("Code".equals(name)) {
            attributes.add(new Code(input, metaData));
        } else if ("StackMapTable".equals(name)) {
            attributes.add(new StackMapTable(input, metaData));
        } else if ("Exceptions".equals(name)) {
            attributes.add(new Exceptions(input, metaData));
        } else if ("InnerClasses".equals(name)) {
            attributes.add(new InnerClasses(input, metaData));
        } else if ("EnclosingMethod".equals(name)) {
            attributes.add(new EnclosingMethod(input, metaData));
        } else if ("Synthetic".equals(name)) {
            attributes.add(new Synthetic(input, metaData));
        } else if ("Signature".equals(name)) {
            attributes.add(new Signature(input, metaData));
        } else if ("SourceFile".equals(name)) {
            attributes.add(new SourceFile(input, metaData));
        } else if ("SourceDebugExtension".equals(name)) {
            attributes.add(new SourceDebugExtension(input, metaData));
        } else if ("LineNumberTable".equals(name)) {
            attributes.add(new LineNumberTable(input, metaData));
        } else if ("LocalVariableTable".equals(name)) {
            attributes.add(new LocalVariableTable(input, metaData));
        } else {
            info("Unknown attribute: " + name + ", skipped");
            skip(input);
        }
    }

    private void skip(InputStream input) throws IOException {
        long length = getAttrLen(input);
        long skipped = input.skip(length);
        assert skipped == length;
    }

    private String getAttrName(InputStream input,
                               ConstantPool constantPool) throws IOException {
        int nameIndex = readBytes2(input);
        return constantPool.getUtf8Str(nameIndex);
    }

    public List<AttributeInfo> getAttributes() {
        return attributes;
    }
}
