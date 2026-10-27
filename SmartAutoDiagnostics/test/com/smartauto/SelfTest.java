package com.smartauto;

import com.smartauto.engine.DiagnosticSession;
import com.smartauto.engine.TreeValidator;
import com.smartauto.knowledge.KnowledgeBaseRegistry;
import com.smartauto.knowledge.VehicleKnowledgeBase;
import com.smartauto.model.Choice;
import com.smartauto.model.DecisionNode;
import com.smartauto.model.Diagnosis;
import com.smartauto.model.QuestionNode;
import com.smartauto.model.ResultNode;
import com.smartauto.model.Symptom;
import com.smartauto.report.ReportPrinter;
import com.smartauto.ui.ConsoleUI;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * اختبارات ذاتية بدون أي مكتبة خارجية.
 * التشغيل: java -cp out com.smartauto.SelfTest
 * ترجع exit code = 1 إذا فشل أي اختبار.
 */
public final class SelfTest {

    private static int passed = 0;
    private static int failed = 0;

    private SelfTest() {
    }

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        VehicleKnowledgeBase tahoe = KnowledgeBaseRegistry.loadAll().get(0);
        Symptom misfire = symptom(tahoe, "MISFIRE");
        Symptom oil = symptom(tahoe, "OIL_TICK");
        Symptom heat = symptom(tahoe, "OVERHEAT");

        // 1) سلامة الأشجار
        for (Symptom s : tahoe.getSymptoms()) {
            TreeValidator.Result r = TreeValidator.validate(s);
            check("الشجرة سليمة: " + s.getId() + " " + r.getErrors(), r.isValid());
            System.out.println("   " + s.getId() + ": أسئلة=" + r.getQuestionCount()
                    + " | أعطال=" + r.getResultCount() + " | أطول مسار=" + r.getMaxDepth());
        }

        // 2) كل المسارات الممكنة تنتهي بنتيجة، وكل نتيجة يمكن الوصول لها
        for (Symptom s : tahoe.getSymptoms()) {
            Set<String> reached = new HashSet<>();
            int paths = exploreAllPaths(tahoe, s, reached);
            Set<String> expected = new HashSet<>();
            collectLeafCodes(s.getRoot(), expected, new HashSet<DecisionNode>());
            check(s.getId() + ": كل نتيجة قابلة للوصول (" + expected.size() + " نتيجة، " + paths + " مسار)",
                    reached.equals(expected));
        }

        // 3) سيناريوهات مرجعية (الإجابات بأرقام الخيارات)
        scenario(tahoe, misfire, "AFM_LIFTER", 1, 1, 1);
        scenario(tahoe, misfire, "COIL", 1, 1, 2, 1);
        scenario(tahoe, misfire, "COIL", 1, 2, 1);
        scenario(tahoe, misfire, "PLUG_INJECTOR_MECH", 1, 2, 2);
        scenario(tahoe, misfire, "COIL_TEST_NEEDED", 1, 2, 3);
        scenario(tahoe, misfire, "COLD_START", 2, 1);
        scenario(tahoe, misfire, "PLUGS_LOAD", 2, 2);
        scenario(tahoe, misfire, "AFM_LIFTER", 2, 3, 1);
        scenario(tahoe, misfire, "FUEL_AIR_GENERAL", 2, 3, 2);
        scenario(tahoe, misfire, "VACUUM_LEAK", 3, 1);
        scenario(tahoe, misfire, "FUEL_SYSTEM", 3, 2, 1);
        scenario(tahoe, misfire, "MAF_DIRTY", 3, 2, 2);
        scenario(tahoe, misfire, "FLASHING_CEL", 4, 1);
        scenario(tahoe, misfire, "SCAN_NEEDED", 4, 2);
        scenario(tahoe, misfire, "PLUGS_OLD", 4, 3, 1);
        scenario(tahoe, misfire, "AFM_LIFTER", 4, 3, 2, 1);

        scenario(tahoe, oil, "OIL_LEAK", 1, 1);
        scenario(tahoe, oil, "OIL_CONSUMPTION", 1, 2);
        scenario(tahoe, oil, "OIL_OVERFILL", 3);
        scenario(tahoe, oil, "ROD_BEARING", 2, 2);
        scenario(tahoe, oil, "REAL_LOW_PRESSURE", 2, 1, 1);
        scenario(tahoe, oil, "OIL_SERVICE", 2, 1, 2, 1);
        scenario(tahoe, oil, "WORN_BEARINGS_PUMP", 2, 1, 2, 2);
        scenario(tahoe, oil, "OIL_SENSOR", 2, 1, 3);
        scenario(tahoe, oil, "OIL_SENSOR", 2, 3, 3);

        scenario(tahoe, heat, "COOLANT_LEAK", 1, 1);
        scenario(tahoe, heat, "HEAD_GASKET", 1, 2);
        scenario(tahoe, heat, "COOLANT_LOSS_UNKNOWN", 1, 3);
        scenario(tahoe, heat, "FAN", 2, 1);
        scenario(tahoe, heat, "THERMOSTAT", 2, 2, 1);
        scenario(tahoe, heat, "FAN", 2, 2, 2, 1);
        scenario(tahoe, heat, "RADIATOR_PUMP", 2, 2, 2, 2);
        scenario(tahoe, heat, "TEMP_SENSOR", 2, 2, 3);

        // 4) سلوك الجلسة
        sessionBehaviour(tahoe, misfire);

        // 5) التقرير
        reportContent(tahoe, misfire);

        // 6) الواجهة النصية بإدخال مبرمج
        consoleHappyPath();
        consoleBackAndInvalidInput();
        consoleArabicDigits();
        consoleSavesReport();
        consoleEndOfInput();

        System.out.println();
        System.out.println("النتيجة: نجح " + passed + " | فشل " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ───────────── اختبارات الجلسة والتقرير ─────────────

    private static void sessionBehaviour(VehicleKnowledgeBase vehicle, Symptom symptom) {
        DiagnosticSession s = new DiagnosticSession(vehicle, symptom);
        check("الجلسة الجديدة غير منتهية", !s.isFinished());
        check("لا رجوع قبل أي إجابة", !s.canGoBack() && !s.goBack());

        s.answer(1);
        s.answer(1);
        check("بعد إجابتين المسار = 2", s.getPath().size() == 2);
        check("الرجوع ينجح", s.goBack());
        check("بعد الرجوع المسار = 1", s.getPath().size() == 1);
        check("السؤال بعد الرجوع هو M_CYL", "M_CYL".equals(s.getCurrentQuestion().getId()));

        expectThrows("رقم خيار 0 مرفوض", IllegalArgumentException.class, () -> s.answer(0));
        expectThrows("رقم خيار كبير مرفوض", IllegalArgumentException.class, () -> s.answer(99));
        expectThrows("لا تشخيص قبل الانتهاء", IllegalStateException.class, s::getDiagnosis);

        s.answer(1);
        s.answer(1);
        check("الجلسة انتهت", s.isFinished());
        expectThrows("لا سؤال بعد الانتهاء", IllegalStateException.class, s::getCurrentQuestion);
        check("التشخيص AFM_LIFTER", "AFM_LIFTER".equals(s.getDiagnosis().getCode()));
    }

    private static void reportContent(VehicleKnowledgeBase vehicle, Symptom symptom) {
        DiagnosticSession s = new DiagnosticSession(vehicle, symptom);
        s.answer(1);
        s.answer(1);
        s.answer(1);
        String report = ReportPrinter.build(s, LocalDateTime.of(2026, 10, 20, 13, 45));
        check("التقرير فيه التاريخ", report.contains("2026-10-20 13:45"));
        check("التقرير فيه اسم المركبة", report.contains(vehicle.getVehicleName()));
        check("التقرير فيه عنوان العطل", report.contains("انهيار رافعة"));
        check("التقرير فيه الحلول", report.contains("طقم إلغاء AFM"));
        check("التقرير فيه مسار الأسئلة", report.contains("عدد الأسئلة: 3"));
        check("التقرير فيه التنبيه", report.contains("تنبيه"));

        DiagnosticSession unfinished = new DiagnosticSession(vehicle, symptom);
        expectThrows("لا تقرير لجلسة غير مكتملة", IllegalStateException.class,
                () -> ReportPrinter.build(unfinished, LocalDateTime.now()));
    }

    // ───────────── اختبارات الواجهة ─────────────

    private static void consoleHappyPath() {
        // عرض 1 (تفتفة) → كود أسطوانة → أسطوانة AFM → تكتكة → لا حفظ → لا تشخيص آخر
        String output = runConsole("1\n1\n1\n1\n2\n2\n", null);
        check("الواجهة: ترحيب", output.contains("وش العرض اللي تواجهه في سيارتك الآن؟"));
        check("الواجهة: نتيجة AFM", output.contains("انهيار رافعة"));
        check("الواجهة: خاتمة", output.contains("سلامتك على الطريق"));
    }

    private static void consoleBackAndInvalidInput() {
        // إدخال خاطئ (abc) ثم رقم خارج النطاق (9) ثم صحيح؛ ثم رجوع (0) وإعادة الإجابة
        String output = runConsole("abc\n1\n9\n1\n0\n1\n1\n1\n2\n2\n", null);
        check("الواجهة: رسالة إدخال غير صحيح", output.contains("إدخال غير صحيح"));
        check("الواجهة: تنتهي بنتيجة بعد الرجوع", output.contains("انهيار رافعة"));
    }

    private static void consoleArabicDigits() {
        // نفس المسار لكن بالأرقام العربية ١ و ٢
        String output = runConsole("١\n١\n١\n١\n٢\n٢\n", null);
        check("الواجهة: تقبل الأرقام العربية", output.contains("انهيار رافعة"));
    }

    private static void consoleSavesReport() {
        try {
            Path dir = Files.createTempDirectory("sad-reports");
            String output = runConsole("1\n1\n1\n1\n1\n2\n", dir);
            try (Stream<Path> files = Files.list(dir)) {
                List<Path> saved = files.collect(Collectors.toList());
                check("الواجهة: تم حفظ ملف واحد", saved.size() == 1);
                String content = new String(Files.readAllBytes(saved.get(0)), StandardCharsets.UTF_8);
                check("الواجهة: الملف فيه التشخيص", content.contains("انهيار رافعة"));
            }
            check("الواجهة: رسالة الحفظ", output.contains("تم حفظ التقرير"));
        } catch (Exception e) {
            check("الواجهة: حفظ التقرير (استثناء: " + e + ")", false);
        }
    }

    private static void consoleEndOfInput() {
        String output = runConsole("", null);
        check("الواجهة: نهاية الإدخال لا تسبب انهيارًا", output.contains("شكرًا لاستخدامك"));
        String midway = runConsole("1\n1\n", null);
        check("الواجهة: نهاية الإدخال في منتصف الجلسة", midway.contains("شكرًا لاستخدامك"));
    }

    private static String runConsole(String input, Path reportsDir) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
            BufferedReader in = new BufferedReader(new StringReader(input));
            Path dir = reportsDir != null ? reportsDir : Files.createTempDirectory("sad-unused");
            new ConsoleUI(in, out, dir).run(KnowledgeBaseRegistry.loadAll());
            return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // ───────────── أدوات مساعدة ─────────────

    private static Symptom symptom(VehicleKnowledgeBase base, String id) {
        for (Symptom s : base.getSymptoms()) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        throw new IllegalArgumentException("عَرَض غير موجود: " + id);
    }

    private static void scenario(VehicleKnowledgeBase vehicle, Symptom symptom, String expectedCode, int... answers) {
        DiagnosticSession s = new DiagnosticSession(vehicle, symptom);
        for (int a : answers) {
            s.answer(a);
        }
        boolean ok = s.isFinished() && expectedCode.equals(s.getDiagnosis().getCode());
        check(symptom.getId() + " " + java.util.Arrays.toString(answers) + " → " + expectedCode, ok);
    }

    /** يجرّب كل المسارات الممكنة عبر الجلسة نفسها، ويرجع عددها. */
    private static int exploreAllPaths(VehicleKnowledgeBase vehicle, Symptom symptom, Set<String> reached) {
        return explore(new DiagnosticSession(vehicle, symptom), reached);
    }

    private static int explore(DiagnosticSession session, Set<String> reached) {
        if (session.isFinished()) {
            Diagnosis d = session.getDiagnosis();
            reached.add(d.getCode());
            return 1;
        }
        int count = 0;
        int options = session.getCurrentQuestion().getChoices().size();
        for (int i = 1; i <= options; i++) {
            session.answer(i);
            count += explore(session, reached);
            session.goBack();
        }
        return count;
    }

    private static void collectLeafCodes(DecisionNode node, Set<String> codes, Set<DecisionNode> seen) {
        if (!seen.add(node)) {
            return;
        }
        if (node instanceof ResultNode) {
            codes.add(((ResultNode) node).getDiagnosis().getCode());
        } else {
            for (Choice c : ((QuestionNode) node).getChoices()) {
                collectLeafCodes(c.getNext(), codes, seen);
            }
        }
    }

    private static void expectThrows(String name, Class<? extends Throwable> type, Runnable action) {
        try {
            action.run();
            check(name, false);
        } catch (Throwable t) {
            check(name, type.isInstance(t));
        }
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
        } else {
            failed++;
            System.out.println("   ✗ فشل: " + name);
        }
    }
}
