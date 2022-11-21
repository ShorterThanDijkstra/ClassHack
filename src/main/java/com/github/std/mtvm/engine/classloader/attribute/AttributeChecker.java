package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.constant.*;

public class AttributeChecker {
    public void checkConstantValue(Constant constant) {
        if (!(constant instanceof ConstantLong ||
                constant instanceof ConstantFloat ||
                constant instanceof ConstantDouble ||
                constant instanceof ConstantInteger ||
                constant instanceof ConstantString)) {
            throw new ClassFormatError();
        }
    }
}
