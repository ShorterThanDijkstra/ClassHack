package com.github.std;

import com.github.std.classhack.ui.Graphic;
import com.github.std.classhack.ui.UI;

public class Main {


    public static void main(String[] args) {
        UI ui = new Graphic();
        if (args.length == 0) {
            ui.show();
        } else if (args.length == 1) {
            ui.show(args[0]);
        } else {
            usage();
        }
    }

    private static void usage() {
        System.out.println("""
                java -jar ClassHack-(version).jar [class file]
                """);

    }
}