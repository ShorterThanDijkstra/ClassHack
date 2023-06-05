package com.github.std.classhack.classreader;

public record ClassParser(String name, ClassParser next, Runnable runnable) {
    void parse() {
        runnable.run();
    }
}
