package com.github.std.classhack.classreader;

public record ClassParser(ClassParser next, Runnable runnable) {
    void parse() {
        runnable.run();
    }
}
