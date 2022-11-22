package com.github.std.mtvm.engine.util;

import java.io.IOException;
import java.io.InputStream;

public class BytesReader {
    public static long byteArrayToLong(byte[] bs) throws IOException {
        long res = 0;
        for (byte b : bs) {
            res = res << 8 | (b & 0Xff);
        }
        return res;
    }

    public static int byteArrayToInt(byte[] bs) {
        int res = 0;
        for (byte b : bs) {
            res = res << 8 | (b & 0XFF);
        }
        return res;
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static int readByte(InputStream input) throws IOException {
        byte[] bytes = new byte[1];
        int read = input.read(bytes);
        assert read == 1;

        return bytes[0] & 0xff;
    }

    public static int readBytes2(InputStream input) throws IOException {
        byte[] bytes = new byte[2];
        int read = input.read(bytes);
        assert read == 2;

        return byteArrayToInt(bytes);
    }

    public static long readBytes4(InputStream input) throws IOException {
        byte[] bytes = new byte[4];
        int read = input.read(bytes);
        assert read == 4;

        return byteArrayToLong(bytes);
    }
}
