package com.github.std.classhack.ui;

import com.github.std.classhack.classreader.ClassFile;

public record ClassFileShow(ClassFile classFile) {
    public String showBasic() {
        return classFile.getThisClass();
    }

    public String showConstantPool() {
//        return classFile.getConstantPool().toString();
        return "cafebabe\n".repeat(10000);
    }

    public String showFieldsMethods() {
        return classFile.getMethodTable().toString();
    }
}
