package com.github.std.classhack.ui;

import com.github.std.classhack.classreader.ClassFile;
import com.github.std.classhack.classreader.attribute.AttributeInfo;
import com.github.std.classhack.classreader.attribute.Code;
import com.github.std.classhack.classreader.attribute.Exceptions;
import com.github.std.classhack.classreader.constant.*;
import com.github.std.classhack.classreader.field.FieldInfo;
import com.github.std.classhack.classreader.method.MethodInfo;

import java.util.List;

public record ClassFileShow(ClassFile classFile, String path) {
    public String showBasic() {
        StringBuilder builder = new StringBuilder();
//        builder.append("path: ").append(path).append("\n\n");
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
                builder.append(flag).append("  ");
            }
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

    private String showCstFieldMethodInterfaceMethodRef(int classIndex, int nameAndTypeIndex) {
        ConstantPool pool = classFile.getConstantPool();
        Constant cstClass = pool.get(classIndex - 1);
        assert cstClass instanceof ConstantClass;
        String className = nameOfCstClass((ConstantClass) cstClass);

        Constant constant = pool.get(nameAndTypeIndex - 1);
        assert constant instanceof ConstantNameAndType;
        ConstantNameAndType nameAndType = (ConstantNameAndType) constant;
        String fieldName = nameOfNameAndType(nameAndType);
        String type = typeOfNameAndType(nameAndType);
        return className + "." + fieldName + ":" + type;
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
                case ConstantClass clazz -> builder.append("Class\t").append(nameOfCstClass(clazz));
                case ConstantFieldRef ref ->
                        builder.append("FieldRef\t").append(showCstFieldMethodInterfaceMethodRef(ref.getClassIndex(), ref.getNameAndTypeIndex()));
                case ConstantInterfaceMethodRef ref ->
                        builder.append("InterfaceMethodRef\t").append(showCstFieldMethodInterfaceMethodRef(ref.getClassIndex(), ref.getNameAndTypeIndex()));
                case ConstantMethodRef ref ->
                        builder.append("MethodRef\t").append(showCstFieldMethodInterfaceMethodRef(ref.getClassIndex(), ref.getNameAndTypeIndex()));
                case ConstantFloat f -> builder.append("Float\t").append(f.getValue());
                case ConstantInteger it -> builder.append("Integer\t").append(it.getValue());
                case ConstantInvokeDynamic cid -> {
                    int bmai = cid.getBootstrapMethodAttrIndex();
                    int nameAndTypeIndex = cid.getNameAndTypeIndex();
                    Constant constant = pool.get(nameAndTypeIndex - 1);
                    assert constant instanceof ConstantNameAndType;
                    ConstantNameAndType nameAndType = (ConstantNameAndType) constant;
                    String fieldName = nameOfNameAndType(nameAndType);
                    String type = typeOfNameAndType(nameAndType);
                    builder.append("InvokeDynamic\t").append(bmai).append(fieldName).append(":").append(type);
                }
                case ConstantMethodHandle cmh -> {
                    int referenceKind = cmh.getReferenceKind();
                    int referenceIndex = cmh.getReferenceIndex();
                    builder.append("MethodHandle\t").append(referenceKind).append("\t").append(referenceIndex);
                }
                case ConstantMethodType cmt -> {
                    int di = cmt.getDescriptorIndex();
                    Constant constant = pool.get(di - 1);
                    assert constant instanceof ConstantUtf8;
                    builder.append("MethodType\t").append(((ConstantUtf8) constant).getValue());
                }
                case ConstantNameAndType cnt -> {
                    String name = nameOfNameAndType(cnt);
                    String type = typeOfNameAndType(cnt);
                    builder.append("NameAndType\t").append(name).append(":").append(type);
                }
                case ConstantString cs -> {
                    int index = cs.getStringIndex();
                    Constant cstUtf8 = pool.get(index - 1);
                    assert cstUtf8 instanceof ConstantUtf8;
                    builder.append("String\t").append(((ConstantUtf8) cstUtf8).getValue());
                }
                case ConstantUtf8 utf8 -> builder.append("UTF8\t").append(utf8.getValue());
                default -> throw new IllegalStateException("Unexpected value: " + cst);
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    public String showFieldsMethods() {
        StringBuilder builder = new StringBuilder();
        List<FieldInfo> fieldInfos = classFile.getFieldTable().getFieldInfos();
        builder.append("Fields:\n");
        for (FieldInfo info : fieldInfos) {
            builder.append("\tname: ").append(info.getName()).append("\n");
            builder.append("\tdescriptor: ").append(info.getDescriptor()).append("\n");
            builder.append("\taccess flags: ");
            for (String flag : info.getAccessFlags()) {
                builder.append(flag).append("  ");
            }
            builder.append("\n");
            builder.append("\n");

        }
        builder.append("\n");

        builder.append("Methods:\n");
        List<MethodInfo> methodInfos = classFile.getMethodTable().getMethodInfos();
        for (MethodInfo info : methodInfos) {
            builder.append("\tname: ").append(info.getName()).append("\n");
            builder.append("\tdescriptor: ").append(info.getDescriptor()).append("\n");
            builder.append("\taccess flags: ");
            for (String flag : info.getAccessFlags()) {
                builder.append(flag).append("  ");
            }
            builder.append("\n");
            List<AttributeInfo> attributes = info.getAttributeTable().getAttributes();
            for (AttributeInfo attr : attributes) {
                switch (attr) {
                    case Code code -> {
                        builder.append("\tcode:\n");
                        builder.append("\t\tmax locals").append(code.getMaxLocals()).append("\t\t");
                        builder.append("max stack").append(code.getMaxStack()).append("\n");
                        for (Code.Opcode opcode : code.getOpcodes()) {
                            builder.append("\t\t").append(opcode.show()).append("\n");
                        }

                        List<Code.ExceptionInfo> exceptionTable = code.getExceptionTable();
                        if (!exceptionTable.isEmpty()){
                            builder.append("\t\texception table:\n");
                            for (Code.ExceptionInfo except : exceptionTable) {
                                builder.append("\t\t\tstart: ").append(except.getStartPc()).append("\n");
                                builder.append("\t\t\tend: ").append(except.getEndPc()).append("\n");
                                builder.append("\t\t\thandle: ").append(except.getEndPc()).append("\n");
                                ConstantClass catchType = except.getCatchType();
                                builder.append("\t\t\tcatch: ").append(nameOfCstClass(catchType)).append("\n");
                                builder.append("\n");
                            }
                        }

                        // TODO: 2023/4/19 Attribute Table of Code
                    }
                    case Exceptions exceptions -> {
                        builder.append("\texceptions: ");
                        for (String exception : exceptions.getExceptions()) {
                            builder.append(exception).append(" ");
                        }
                        builder.append("\n");
                    }
                    default -> System.out.println("TODO: " + attr.getClass().getName());
                }
            }
            builder.append("\n");
            builder.append("\n");
        }
        return builder.toString();
    }
}
