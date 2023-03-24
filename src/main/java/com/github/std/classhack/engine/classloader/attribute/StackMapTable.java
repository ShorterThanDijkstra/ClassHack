package com.github.std.classhack.engine.classloader.attribute;

import com.github.std.classhack.engine.classloader.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static com.github.std.classhack.engine.util.BytesReader.readByte;
import static com.github.std.classhack.engine.util.BytesReader.readBytes2;

// thanks to this blog: https://blog.csdn.net/qq_26000415/article/details/94401129
public final class StackMapTable implements AttributeInfo {
    private final long attrLen;
    private final List<StackMapFrame> entries;

    private StackMapTable(long attrLen, List<StackMapFrame> entries) {
        this.attrLen = attrLen;
        this.entries = entries;
    }

    interface VerificationType {

        static VerificationType parseVeriType(InputStream input) throws IOException {
            int tag = readByte(input);
            if (tag == 0) {
                return new TopVariable();
            }
            if (tag == 1) {
                return new IntVariable();
            }
            if (tag == 2) {
                return new FloatVariable();
            }
            if (tag == 5) {
                return new NullVariable();
            }
            if (tag == 6) {
                return new UninitializedThisVariable();
            }
            if (tag == 7) {
                return new ObjectVariable(readBytes2(input));
            }
            if (tag == 8) {
                return new UninitializedVariable(readBytes2(input));
            }
            if (tag == 4) {
                return new LongVariable();
            }
            if (tag == 3) {
                return new DoubleVariable();
            }
            throw new ClassFormatError();
        }
    }

    final static class TopVariable implements VerificationType {

    }

    final static class IntVariable implements VerificationType {

    }

    final static class FloatVariable implements VerificationType {

    }

    final static class LongVariable implements VerificationType {

    }

    final static class DoubleVariable implements VerificationType {

    }

    final static class NullVariable implements VerificationType {

    }

    final static class UninitializedThisVariable implements VerificationType {

    }

    final static class ObjectVariable implements VerificationType {
        private final int cpoolIndex;

        private ObjectVariable(int cpoolIndex) {
            this.cpoolIndex = cpoolIndex;
        }

        public int getCpoolIndex() {
            return cpoolIndex;
        }
    }

    final static class UninitializedVariable implements VerificationType {
        private final int offset;

        public UninitializedVariable(int offset) {
            this.offset = offset;
        }

        public int getOffset() {
            return offset;
        }
    }

    private interface StackMapFrame {
        static StackMapFrame parseFrame(InputStream input) throws IOException {
            int tag = readByte(input);
            if (tag == -1) {
                throw new ClassFormatError();
            }
            if (SameFrame.checkTag(tag)) {
                return SameFrame.parse(tag);
            }
            if (SameLocals1StackItemFrame.checkTag(tag)) {
                return SameLocals1StackItemFrame.parse(tag, input);
            }
            if (SameLocals1StackItemFrameExtended.checkTag(tag)) {
                return SameLocals1StackItemFrameExtended.parse(input);
            }
            if (ChopFrame.checkTag(tag)) {
                return ChopFrame.parse(tag, input);
            }
            if (SameFrameExtended.checkTag(tag)) {
                return SameFrameExtended.parse(input);
            }
            if (AppendFrame.checkTag(tag)) {
                return AppendFrame.parse(tag, input);
            }
            if (FullFrame.checkTag(tag)) {
                return FullFrame.parse(input);
            }
            throw new ClassFormatError();

        }
    }

    private static final class SameFrame implements StackMapFrame {
        private final int offsetDelta;

        private SameFrame(int tag) {
            this.offsetDelta = tag;
        }

        public static SameFrame parse(int tag) {
            return new SameFrame(tag);
        }

        int getOffsetDelta() {
            return offsetDelta;
        }

        static boolean checkTag(int tag) {
            return tag <= 63 && tag >= 0;
        }
    }

    static final class SameLocals1StackItemFrame implements StackMapFrame {
        private final Deque<VerificationType> vtStack;
        private final int offsetDelta;

        private SameLocals1StackItemFrame(Deque<VerificationType> vtStack, int offsetDelta) {
            this.vtStack = vtStack;
            this.offsetDelta = offsetDelta;
        }

        static boolean checkTag(int tag) {
            return tag <= 127 && tag >= 64;
        }

        public static SameLocals1StackItemFrame parse(int tag, InputStream input) throws IOException {
            int offsetDelta = tag - 64;

            Deque<VerificationType> vtStack = new ArrayDeque<>(1);
            vtStack.push(VerificationType.parseVeriType(input));
            return new SameLocals1StackItemFrame(vtStack, offsetDelta);
        }

        public Deque<VerificationType> getVtStack() {
            return vtStack;
        }

        public int getOffsetDelta() {
            return offsetDelta;
        }
    }

    static final class SameLocals1StackItemFrameExtended implements StackMapFrame {
        private final int offsetDelta;
        private final Deque<VerificationType> vtStack;

        public SameLocals1StackItemFrameExtended(int offsetDelta, Deque<VerificationType> vtStack) {
            this.offsetDelta = offsetDelta;
            this.vtStack = vtStack;
        }

        public static SameLocals1StackItemFrameExtended parse(InputStream input) throws IOException {
            int offsetDelta = readBytes2(input);
            Deque<VerificationType> vtStack = new ArrayDeque<>(1);
            vtStack.push(VerificationType.parseVeriType(input));
            return new SameLocals1StackItemFrameExtended(offsetDelta, vtStack);
        }

        public int getOffsetDelta() {
            return offsetDelta;
        }

        public Deque<VerificationType> getVtStack() {
            return vtStack;
        }

        static boolean checkTag(int tag) {
            return tag == 247;
        }
    }

    static final class ChopFrame implements StackMapFrame {
        private final int kLocalVars;
        private final int offsetDelta;

        private ChopFrame(int kLocalVars, int offsetDelta) {
            this.kLocalVars = kLocalVars;
            this.offsetDelta = offsetDelta;
        }

        public static ChopFrame parse(int tag, InputStream input) throws IOException {
            int kLocalVars = 251 - tag;
            int offsetDelta = readBytes2(input);
            return new ChopFrame(kLocalVars, offsetDelta);
        }

        static boolean checkTag(int tag) {
            return tag <= 250 && tag >= 248;
        }

        public int getkLocalVars() {
            return kLocalVars;
        }

        public int getOffsetDelta() {
            return offsetDelta;
        }
    }

    static final class SameFrameExtended implements StackMapFrame {
        private final int offsetDelta;

        public SameFrameExtended(int offsetDelta) {
            this.offsetDelta = offsetDelta;
        }

        static SameFrameExtended parse(InputStream input) throws IOException {
            int offsetDelta = readBytes2(input);
            return new SameFrameExtended(offsetDelta);
        }

        static boolean checkTag(int tag) {
            return tag == 251;
        }

        public int getOffsetDelta() {
            return offsetDelta;
        }
    }

    static final class AppendFrame implements StackMapFrame {
        private final int offsetDelta;
        private final Deque<VerificationType> vtStack;

        private AppendFrame(int offsetDelta, Deque<VerificationType> vtStack) {
            this.offsetDelta = offsetDelta;
            this.vtStack = vtStack;
        }

        static AppendFrame parse(int tag, InputStream input) throws IOException {
            int offsetDelta = readBytes2(input);
            Deque<VerificationType> vtStack = new ArrayDeque<>(tag - 251);
            for (int i = 0; i < tag - 251; i++) {
                vtStack.push(VerificationType.parseVeriType(input));
            }
            return new AppendFrame(offsetDelta, vtStack);
        }

        static boolean checkTag(int tag) {
            return tag <= 254 && tag >= 252;
        }

        public int getOffsetDelta() {
            return offsetDelta;
        }

        public Deque<VerificationType> getVtStack() {
            return vtStack;
        }
    }

    static final class FullFrame implements StackMapFrame {
        private final int offsetDelta;
        private final Deque<VerificationType> locals;
        private final Deque<VerificationType> stack;

        private FullFrame(int offsetDelta, Deque<VerificationType> locals, Deque<VerificationType> stack) {
            this.offsetDelta = offsetDelta;
            this.locals = locals;
            this.stack = stack;
        }

        static FullFrame parse(InputStream input) throws IOException {
            int offsetDelta = readBytes2(input);

            int localNum = readBytes2(input);
            Deque<VerificationType> locals = new ArrayDeque<>(localNum);
            for (int i = 0; i < localNum; i++) {
                locals.push(VerificationType.parseVeriType(input));
            }

            int stackItemNum = readBytes2(input);
            Deque<VerificationType> stack = new ArrayDeque<>(stackItemNum);
            for (int i = 0; i < stackItemNum; i++) {
                stack.push(VerificationType.parseVeriType(input));
            }
            return new FullFrame(offsetDelta, locals, stack);
        }


        static boolean checkTag(int tag) {
            return tag == 255;
        }

        public int getOffsetDelta() {
            return offsetDelta;
        }

        public Deque<VerificationType> getLocals() {
            return locals;
        }

        public Deque<VerificationType> getStack() {
            return stack;
        }
    }

    public static StackMapTable parse(InputStream input, ClassFile.ClassFileBuilder metaData) throws IOException {
        long attrLen = AttributeTable.getAttrLen(input);

        int entriesNum = readBytes2(input);
        List<StackMapFrame> entries = new ArrayList<>(entriesNum);

        for (int i = 0; i < entriesNum; i++) {
            entries.add(StackMapFrame.parseFrame(input));
        }
        return new StackMapTable(attrLen, entries);
    }

    public long getAttrLen() {
        return attrLen;
    }

    public List<StackMapFrame> getEntries() {
        return entries;
    }
}