package com.smartauto.ui;

import com.smartauto.engine.DiagnosticSession;
import com.smartauto.knowledge.VehicleKnowledgeBase;
import com.smartauto.model.Choice;
import com.smartauto.model.QuestionNode;
import com.smartauto.model.Symptom;
import com.smartauto.report.ReportPrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

/**
 * الواجهة النصية: تعرض الأسئلة وتقرأ الإجابات.
 * لا تحتوي أي منطق تشخيص؛ كل المنطق في {@link DiagnosticSession}.
 */
public class ConsoleUI {

    /** يُرمى عندما ينتهي الإدخال (مثل Ctrl+D) لإنهاء البرنامج بهدوء. */
    static final class InputEndedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private static final String LINE = "──────────────────────────────────────────────";

    private final BufferedReader in;
    private final PrintStream out;
    private final Path reportsDir;

    public ConsoleUI(BufferedReader in, PrintStream out, Path reportsDir) {
        this.in = in;
        this.out = out;
        this.reportsDir = reportsDir;
    }

    public void run(List<VehicleKnowledgeBase> knowledgeBases) {
        printBanner();
        try {
            VehicleKnowledgeBase vehicle = chooseVehicle(knowledgeBases);
            while (true) {
                Symptom symptom = chooseSymptom(vehicle);
                if (symptom == null) {
                    break;
                }
                DiagnosticSession session = new DiagnosticSession(vehicle, symptom);
                if (!runSession(session)) {
                    continue; // رجع المستخدم لقائمة الأعراض
                }
                showReport(session);
                if (!askYesNo("هل تريد تشخيص عرض آخر؟")) {
                    break;
                }
            }
        } catch (InputEndedException e) {
            out.println();
        }
        out.println();
        out.println("شكرًا لاستخدامك Smart Auto Diagnostics — سلامتك على الطريق!");
    }

    // ───────────── الخطوات ─────────────

    private void printBanner() {
        out.println("══════════════════════════════════════════════");
        out.println("   نظام التشخيص الميكانيكي الذكي");
        out.println("   Smart Auto Diagnostics");
        out.println("══════════════════════════════════════════════");
        out.println("أهلًا بك! أنا «المهندس الآلي»، أسألك أسئلة بسيطة وأوصلك للعطل المحتمل.");
        out.println();
    }

    private VehicleKnowledgeBase chooseVehicle(List<VehicleKnowledgeBase> bases) {
        if (bases.size() == 1) {
            out.println("المركبة: " + bases.get(0).getVehicleName());
            return bases.get(0);
        }
        out.println("اختر المركبة:");
        for (int i = 0; i < bases.size(); i++) {
            out.println("  " + (i + 1) + " - " + bases.get(i).getVehicleName());
        }
        int n = readNumber("اختيارك", 1, bases.size());
        return bases.get(n - 1);
    }

    /** يرجع العَرَض المختار، أو null إذا اختار المستخدم الخروج. */
    private Symptom chooseSymptom(VehicleKnowledgeBase vehicle) {
        List<Symptom> symptoms = vehicle.getSymptoms();
        out.println();
        out.println("وش العرض اللي تواجهه في سيارتك الآن؟");
        for (int i = 0; i < symptoms.size(); i++) {
            Symptom s = symptoms.get(i);
            out.println("  " + (i + 1) + " - " + s.getTitle());
            out.println("      " + s.getDescription());
        }
        out.println("  0 - خروج");
        int n = readNumber("اختيارك", 0, symptoms.size());
        return n == 0 ? null : symptoms.get(n - 1);
    }

    /** يمشي في الأسئلة حتى النتيجة. يرجع false إذا رجع المستخدم من أول سؤال. */
    private boolean runSession(DiagnosticSession session) {
        while (!session.isFinished()) {
            QuestionNode q = session.getCurrentQuestion();
            int questionNumber = session.getPath().size() + 1;

            out.println();
            out.println(LINE);
            out.println("السؤال " + questionNumber + ": " + q.getText());
            if (q.getHint() != null) {
                out.println("(توضيح: " + q.getHint() + ")");
            }
            List<Choice> choices = q.getChoices();
            for (int i = 0; i < choices.size(); i++) {
                out.println("  " + (i + 1) + " - " + choices.get(i).getLabel());
            }
            out.println("  0 - " + (session.canGoBack() ? "رجوع للسؤال السابق" : "رجوع لقائمة الأعراض"));

            int n = readNumber("إجابتك", 0, choices.size());
            if (n == 0) {
                if (!session.goBack()) {
                    return false;
                }
            } else {
                session.answer(n);
            }
        }
        return true;
    }

    private void showReport(DiagnosticSession session) {
        LocalDateTime now = LocalDateTime.now();
        String report = ReportPrinter.build(session, now);
        out.println();
        out.print(report);
        out.println();
        if (askYesNo("هل تريد حفظ التقرير في ملف؟")) {
            try {
                Path file = ReportPrinter.save(report, reportsDir, now);
                out.println("تم حفظ التقرير في: " + file.toAbsolutePath());
            } catch (IOException e) {
                out.println("تعذّر حفظ التقرير: " + e.getMessage());
            }
        }
    }

    // ───────────── قراءة الإدخال ─────────────

    private boolean askYesNo(String question) {
        out.println();
        out.println(question);
        out.println("  1 - نعم");
        out.println("  2 - لا");
        return readNumber("اختيارك", 1, 2) == 1;
    }

    /** يقرأ رقمًا ضمن النطاق، ويعيد السؤال عند الخطأ. يقبل الأرقام العربية (١٢٣). */
    private int readNumber(String prompt, int min, int max) {
        while (true) {
            out.print(prompt + " [" + min + "-" + max + "]: ");
            out.flush();
            String line;
            try {
                line = in.readLine();
            } catch (IOException e) {
                throw new InputEndedException();
            }
            if (line == null) {
                throw new InputEndedException();
            }
            Integer value = parseNumber(line);
            if (value != null && value >= min && value <= max) {
                return value;
            }
            out.println("إدخال غير صحيح، اكتب رقمًا بين " + min + " و " + max + ".");
        }
    }

    /** يحوّل النص إلى رقم (مع دعم الأرقام العربية-الهندية)، أو null إن لم يكن رقمًا. */
    static Integer parseNumber(String text) {
        String trimmed = text.trim();
        if (trimmed.isEmpty() || trimmed.length() > 4) {
            return null;
        }
        int value = 0;
        for (int i = 0; i < trimmed.length(); i++) {
            int digit = Character.digit(trimmed.charAt(i), 10);
            if (digit < 0) {
                return null;
            }
            value = value * 10 + digit;
        }
        return value;
    }
}
