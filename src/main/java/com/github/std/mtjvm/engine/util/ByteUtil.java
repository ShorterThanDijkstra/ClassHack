package com.github.std.mtjvm.engine.util;

public class ByteUtil {
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

    public static void main(String[] args) {
        byte[] high = new byte[] {0x7f, (byte) 0xf0, 0, 0};
        byte[] low = new byte[] {0, 0, 0, 0};

        long bits = byteArrayToInt(high);
        bits = bits << 32 | byteArrayToInt(low);

        System.out.println(Double.longBitsToDouble(bits));
    }
}
