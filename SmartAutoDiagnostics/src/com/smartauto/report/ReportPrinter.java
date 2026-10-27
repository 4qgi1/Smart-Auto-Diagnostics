package com.smartauto.report;

import com.smartauto.engine.AnswerStep;
import com.smartauto.engine.DiagnosticSession;
import com.smartauto.model.Diagnosis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * يبني نص التقرير النهائي من جلسة مكتملة، ويحفظه في ملف عند الطلب.
 */
public final class ReportPrinter {

    private static final String NL = System.lineSeparator();
    private static final String DOUBLE_LINE = repeat('═', 56);
    private static final String LINE = repeat('─', 56);
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ENGLISH);
    private static final DateTimeFormatter FILE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.ENGLISH);

    private ReportPrinter() {
    }

    public static String build(DiagnosticSession session, LocalDateTime when) {
        if (!session.isFinished()) {
            throw new IllegalStateException("لا يمكن إنشاء تقرير قبل اكتمال التشخيص");
        }
        Diagnosis d = session.getDiagnosis();
        List<AnswerStep> path = session.getPath();

        StringBuilder sb = new StringBuilder();
        sb.append(DOUBLE_LINE).append(NL);
        sb.append("   تقرير التشخيص — Smart Auto Diagnostics").append(NL);
        sb.append(DOUBLE_LINE).append(NL);
        sb.append("التاريخ: ").append(DATE_FORMAT.format(when)).append(NL);
        sb.append("المركبة: ").append(session.getVehicle().getVehicleName()).append(NL);
        sb.append("العرض: ").append(session.getSymptom().getTitle()).append(NL);
        sb.append(LINE).append(NL);

        sb.append("مسار التحقيق (عدد الأسئلة: ").append(path.size()).append(")").append(NL);
        for (AnswerStep step : path) {
            sb.append("• س: ").append(step.getQuestionText()).append(NL);
            sb.append("  ج: ").append(step.getAnswerLabel()).append(NL);
        }
        sb.append(LINE).append(NL);

        sb.append("التشخيص المحتمل: ").append(d.getTitle()).append(NL);
        sb.append("درجة اليقين: ").append(d.getConfidence().getLabel()).append(NL);
        sb.append("درجة الخطورة: ").append(d.getSeverity().getLabel())
                .append(" — ").append(d.getSeverity().getAdvice()).append(NL);
        sb.append(NL);
        sb.append("الشرح:").append(NL);
        sb.append("  ").append(d.getExplanation()).append(NL);
        sb.append(NL);
        sb.append("الحل المقترح:").append(NL);
        for (String solution : d.getSolutions()) {
            sb.append("• ").append(solution).append(NL);
        }
        if (d.getTip() != null && !d.getTip().trim().isEmpty()) {
            sb.append(NL);
            sb.append("ملاحظة: ").append(d.getTip()).append(NL);
        }
        sb.append(LINE).append(NL);
        sb.append("تنبيه: هذا تشخيص مبدئي مبني على إجاباتك ولا يغني عن فحص فني. ")
                .append("تأكّد من العطل في ورشة متخصصة قبل شراء القطع أو تنفيذ أي إصلاح.").append(NL);
        sb.append(DOUBLE_LINE).append(NL);
        return sb.toString();
    }

    /** يحفظ التقرير في مجلد معيّن باسم يحمل التاريخ والوقت، ويرجع مسار الملف. */
    public static Path save(String report, Path directory, LocalDateTime when) throws IOException {
        Files.createDirectories(directory);
        Path file = directory.resolve("report_" + FILE_FORMAT.format(when) + ".txt");
        Files.write(file, report.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private static String repeat(char c, int times) {
        StringBuilder sb = new StringBuilder(times);
        for (int i = 0; i < times; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
