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

    private AttributeTable(List<AttributeInfo> attributes) {
        this.attributes = attributes;
    }


    public static AttributeTable parse(int attrCount, InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        List<AttributeInfo> attributes = new ArrayList<>(attrCount);
        for (int i = 0; i < attrCount; i++) {
            AttributeInfo attr = parseAttr(input, metaData);
            if (!(attr instanceof Unknown)) {
                attributes.add(attr);
            }
        }
        return new AttributeTable(attributes);
    }

    private static AttributeInfo parseAttr(InputStream input,
                                           ClassFile.ClassFileBuilder metaData) throws IOException {
        String name = getAttrName(input, metaData.constantPool);
        if ("ConstantValue".equals(name)) {
            return ConstantValue.parse(input, metaData);
        } else if ("Code".equals(name)) {
            return Code.parse(input, metaData);
        } else if ("StackMapTable".equals(name)) {
            return StackMapTable.parse(input, metaData);
        } else if ("Exceptions".equals(name)) {
            return Exceptions.parse(input, metaData);
        } else if ("InnerClasses".equals(name)) {
            return InnerClasses.parse(input, metaData);
        } else if ("EnclosingMethod".equals(name)) {
            return EnclosingMethod.parse(input, metaData);
        } else if ("Synthetic".equals(name)) {
            return Synthetic.parse(input, metaData);
        } else if ("Signature".equals(name)) {
            return Signature.parse(input, metaData);
        } else if ("SourceFile".equals(name)) {
            return SourceFile.parse(input, metaData);
        } else if ("SourceDebugExtension".equals(name)) {
            return SourceDebugExtension.parse(input, metaData);
        } else if ("LineNumberTable".equals(name)) {
            return LineNumberTable.parse(input, metaData);
        } else if ("LocalVariableTable".equals(name)) {
            return LocalVariableTable.parse(input, metaData);
        } else if ("LocalVariableTypeTable".equals(name)) {
            return LocalVariableTypeTable.parse(input, metaData);
        } else if ("Deprecated".equals(name)) {
            return Deprecated.parse(input, metaData);
        } else if ("RuntimeVisibleAnnotations".equals(name)) {
            return RuntimeVisibleAnnotations.parse(input, metaData);
        } else if ("RuntimeInvisibleAnnotations".equals(name)) {
            return RuntimeInvisibleAnnotations.parse(input, metaData);
        } else if ("RuntimeVisibleParameterAnnotations".equals(name)) {
            return RuntimeVisibleParameterAnnotations.parse(input, metaData);
        } else if ("RuntimeInvisibleParameterAnnotations".equals(name)) {
            return RuntimeInvisibleParameterAnnotations.parse(input, metaData);
        } else if ("RuntimeVisibleTypeAnnotations".equals(name)) {
            return RuntimeVisibleTypeAnnotations.parse(input, metaData);
        } else {
            return skipped(input, name);
        }
    }

    private static AttributeInfo skipped(InputStream input, String name) throws IOException {
        info("Unknown attribute: " + name + ", skipped");
        long length = getAttrLen(input);
        long skipped = input.skip(length);
        assert skipped == length;
        return new Unknown(name);
    }

    private static String getAttrName(InputStream input,
                                      ConstantPool constantPool) throws IOException {
        int nameIndex = readBytes2(input);
        return constantPool.getUtf8Str(nameIndex);
    }

    public List<AttributeInfo> getAttributes() {
        return attributes;
    }
}
