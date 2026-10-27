package com.smartauto;

import com.smartauto.knowledge.KnowledgeBaseRegistry;
import com.smartauto.ui.ConsoleUI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * نقطة تشغيل البرنامج.
 * نضبط الترميز على UTF-8 ليظهر النص العربي بشكل سليم.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        ConsoleUI ui = new ConsoleUI(in, out, Paths.get("reports"));
        ui.run(KnowledgeBaseRegistry.loadAll());
    }
}
