package com.github.std.mtvm.engine.classloader.attribute;

import com.github.std.mtvm.engine.classloader.ClassFile;
import com.github.std.mtvm.engine.classloader.constant.Constant;
import com.github.std.mtvm.engine.classloader.constant.ConstantMethodHandle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.std.mtvm.engine.classloader.attribute.AttributeChecker.checkBootstrapMethodsArgument;
import static com.github.std.mtvm.engine.classloader.attribute.AttributeChecker.checkBootstrapMethodsMethodRef;
import static com.github.std.mtvm.engine.classloader.attribute.AttributeTable.getAttrLen;
import static com.github.std.mtvm.engine.util.BytesReader.readBytes2;

public final class BootstrapMethods implements AttributeInfo {
    private final List<BootMethod> bootMethods;

    public BootstrapMethods(List<BootMethod> bootMethods) {
        this.bootMethods = bootMethods;
    }

    public List<BootMethod> getBootMethods() {
        return bootMethods;
    }

    private static final class BootMethod {
        private final ConstantMethodHandle methodHandle;
        private final List<Constant> arguments;

        public BootMethod(ConstantMethodHandle methodHandle, List<Constant> arguments) {
            this.methodHandle = methodHandle;
            this.arguments = arguments;
        }

        public ConstantMethodHandle getMethodHandle() {
            return methodHandle;
        }

        public List<Constant> getArguments() {
            return arguments;
        }

        public static BootMethod parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
            int methodRef = readBytes2(input);
            final ConstantMethodHandle methodHandle = checkBootstrapMethodsMethodRef(methodRef, metaData.constantPool);

            int argNum = readBytes2(input);
            final List<Constant> arguments = new ArrayList<>(argNum);
            for (int i = 0; i < argNum; i++) {
                int argIndex = readBytes2(input);
                arguments.add(checkBootstrapMethodsArgument(argIndex, metaData.constantPool));
            }
            return new BootMethod(methodHandle, arguments);
        }


    }

    public static BootstrapMethods parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long len = getAttrLen(input);
        int bootMethodsNum = readBytes2(input);

        final List<BootMethod> bootMethods = new ArrayList<>(bootMethodsNum);
        for (int i = 0; i < bootMethodsNum; i++) {
            bootMethods.add(BootMethod.parse(input, metaData));
        }
        return new BootstrapMethods(bootMethods);
    }
}