package com.github.std.classhack.classreader;

/**
 * test switch
 */
public class Class1 {


    static int test0(int var0) {
        switch (var0) {
            case 7:
                return 0;
            case 9:
                return 2;
            case 6:
                return 3;
            default:
                return -1;
        }
    }

    int test1(int var0) {
        switch (var0) {
            case 1000:
                return 0;
            case 100:
                return 1;
            case 10:
                return 2;
            default:
                return -1;
        }
    }
}
