package com.github.std.classhack.ui;

import com.github.std.classhack.classreader.ClassFile;
import com.github.std.classhack.classreader.constant.*;

import java.util.List;

public record ClassFileShow(ClassFile classFile) {
    public String showBasic() {
        StringBuilder builder = new StringBuilder();
        builder.append("major version: ").append(classFile.getMajorVersion()).append("\n\n");
        builder.append("minor version: ").append(classFile.getMinorVersion()).append("\n\n");
        builder.append("class name: ").append(classFile.getThisClass()).append("\n\n");
        builder.append("super class: ").append(classFile.getSuperClass()).append("\n\n");

        if (classFile.getInterfaces().length != 0) {
            builder.append("interfaces: ");
            for (String itf : classFile.getInterfaces()) {
                builder.append(itf).append(", ");
            }
            builder.setLength(builder.length() - 2);
            builder.append("\n\n");
        }
        if (classFile.getAccessFlags().size() != 0) {
            builder.append("access flags: ");
            for (String flag : classFile.getAccessFlags()) {
                builder.append(flag).append(", ");
            }
            builder.setLength(builder.length() - 2);
            builder.append("\n\n");
        }
        return builder.toString();
    }

    private String nameOfCstClass(ConstantClass cstClass) {
        int nameIndex = cstClass.getNameIndex();
        Constant constant = classFile.getConstantPool().get(nameIndex - 1);
        assert constant instanceof ConstantUtf8;
        return ((ConstantUtf8) constant).getValue();
    }

    private String nameOfNameAndType(ConstantNameAndType cstNameAndTy) {
        int nameIndex = cstNameAndTy.getNameIndex();
        Constant constant = classFile.getConstantPool().get(nameIndex - 1);
        assert constant instanceof ConstantUtf8;
        return ((ConstantUtf8) constant).getValue();

    }

    private String typeOfNameAndType(ConstantNameAndType cstNameAndTy) {
        int descriptorIndex = cstNameAndTy.getDescriptorIndex();
        Constant constant = classFile.getConstantPool().get(descriptorIndex - 1);
        assert constant instanceof ConstantUtf8;
        return ((ConstantUtf8) constant).getValue();
    }

    public String showConstantPool() {
        List<Constant> pool = classFile.getConstantPool().getPool();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < pool.size(); i++) {
            Constant cst = pool.get(i);
            builder.append(i + 1).append("\t");
            switch (cst) {
                case null -> throw new IllegalStateException("Unexpected value: " + null);
                case ConstantDouble d -> {
                    builder.append("Double\t").append(d.getValue());
                    i++;
                }
                case ConstantLong l -> {
                    builder.append("Long\t").append(l.getValue());
                    i++;
                }
                case ConstantClass clazz -> {
                    builder.append("Class\t").append(nameOfCstClass(clazz));
                }
                case ConstantFieldRef fieldRef -> {
                    int classIndex = fieldRef.getClassIndex();
                    Constant cstClass = pool.get(classIndex - 1);
                    assert cstClass instanceof ConstantClass;
                    String className = nameOfCstClass((ConstantClass) cstClass);

                    int nameAndTypeIndex = fieldRef.getNameAndTypeIndex();
                    Constant constant = pool.get(nameAndTypeIndex - 1);
                    assert constant instanceof ConstantNameAndType;
                    ConstantNameAndType nameAndType = (ConstantNameAndType) constant;
                    String fieldName = nameOfNameAndType(nameAndType);
                    String type = typeOfNameAndType(nameAndType);

                    builder.append(className)
                            .append(".")
                            .append(fieldName)
                            .append(":")
                            .append(type);
                }
                case ConstantFloat f -> builder.append("Float\t").append(f.getValue());
                case ConstantInteger it -> builder.append("Integer\t").append(it.getValue());
                default -> builder.append("Unexpected value: ").append(cst);
//                default -> throw new IllegalStateException("Unexpected value: " + cst);
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    public String showFieldsMethods() {
        return classFile.getMethodTable().toString();
    }
}
