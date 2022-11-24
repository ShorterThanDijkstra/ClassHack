package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.ClassFile;
import com.github.std.mtvm.engine.classloader.constant.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtvm.engine.classloader.attribute.AttributeChecker.checkConstElementValueTag;
import static com.github.std.mtvm.engine.util.BytesReader.readByte;
import static com.github.std.mtvm.engine.util.BytesReader.readBytes2;

public final class AnnoElementValuePair {
    private final String name;
    private final ElementValue value;

    public static AnnoElementValuePair parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        int nameIndex = readBytes2(input);
        String name = metaData.constantPool.getUtf8Str(nameIndex);
        ElementValue elementValue = ElementValue.parse(input, metaData);
        return new AnnoElementValuePair(name, elementValue);
    }

    public String getName() {
        return name;
    }

    public ElementValue getValue() {
        return value;
    }

    public AnnoElementValuePair(String name, ElementValue value) {
        this.name = name;
        this.value = value;
    }

    private interface ElementValue {

        static ElementValue parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
            char tag = (char) readByte(input);
            if (tag == 'B' || tag == 'C' || tag == 'D' || tag == 'F' || tag == 'I' || tag == 'J' || tag == 'S' || tag == 'Z' || tag == 's') {
                return ConstElementValue.parseValue(input, metaData, tag);
            }
            if (tag == 'e') {
                return EnumElementValue.parseValue(input, metaData);
            }
            if (tag == 'c') {
                return ClassInfoElementValue.parseValue(input, metaData);
            }
            if (tag == '@') {
                return AnnoElementValue.parseValue(input, metaData);
            }
            if (tag == '[') {
                return ArrayElementValue.parseValue(input, metaData);
            }
            throw new ClassFormatError("Unknown ELEMENT_VALUE tag");
        }
    }

    private final static class ConstElementValue implements ElementValue {
        // primitive types or String
        private final Constant value;

        private ConstElementValue(Constant value) {
            this.value = value;
        }

        public static ConstElementValue parseValue(InputStream input, ClassFile.ClassFileBuilder metaData, char tag) throws IOException {
            int constIndex = readBytes2(input);
            Constant constant = metaData.constantPool.get(constIndex - 1);
            checkConstElementValueTag(tag, constant);
            return new ConstElementValue(constant);
        }

        public Constant getValue() {
            return value;
        }
    }

    private final static class EnumElementValue implements ElementValue {
        private final String typeName;
        private final String constName;

        private EnumElementValue(String typeName, String constName) {
            this.typeName = typeName;
            this.constName = constName;
        }

        public String getTypeName() {
            return typeName;
        }

        public String getConstName() {
            return constName;
        }

        public static EnumElementValue parseValue(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
            int typeNameIndex = readBytes2(input);
            int constNameIndex = readBytes2(input);

            String typeName = metaData.constantPool.getUtf8Str(typeNameIndex);
            String constName = metaData.constantPool.getUtf8Str(constNameIndex);

            return new EnumElementValue(typeName, constName);
        }
    }

    private final static class ClassInfoElementValue implements ElementValue {
        private final String desc;

        public ClassInfoElementValue(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        public static ClassInfoElementValue parseValue(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
            int classInfoIndex = readBytes2(input);
            String desc = metaData.constantPool.getUtf8Str(classInfoIndex);
            return new ClassInfoElementValue(desc);
        }
    }

    private final static class AnnoElementValue implements ElementValue {
        private final Anno anno;

        public AnnoElementValue(Anno anno) {
            this.anno = anno;
        }

        public Anno getAnno() {
            return anno;
        }

        public static AnnoElementValue parseValue(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
            Anno anno = Anno.parse(input, metaData);
            return new AnnoElementValue(anno);
        }
    }

    private final static class ArrayElementValue implements ElementValue {
        private final List<ElementValue> elementValues;

        private ArrayElementValue(List<ElementValue> elementValues) {
            this.elementValues = elementValues;
        }

        public List<ElementValue> getElementValues() {
            return elementValues;
        }

        public static ArrayElementValue parseValue(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
            int valuesNum = readBytes2(input);
            List<ElementValue> elementValues = new ArrayList<>(valuesNum);
            for (int i = 0; i < valuesNum; i++) {
                elementValues.add(ElementValue.parse(input, metaData));
            }
            return new ArrayElementValue(elementValues);
        }
    }
}


