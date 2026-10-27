package com.smartauto.model;

/**
 * درجة خطورة العطل، ومعها نصيحة جاهزة تُطبع في التقرير النهائي.
 */
public enum Severity {
    LOW("منخفضة", "يمكنك القيادة بحذر وإصلاح العطل في أقرب فرصة."),
    MEDIUM("متوسطة", "يفضّل إصلاحه قريبًا؛ القيادة لمسافات قصيرة مقبولة مع المراقبة."),
    HIGH("عالية", "تجنّب القيادة لمسافات طويلة وراجع ورشة متخصصة بأسرع وقت."),
    CRITICAL("حرجة", "أوقف المحرك فورًا ولا تقد السيارة؛ استخدم سطحة (ونش) إلى الورشة.");

    private final String label;
    private final String advice;

    Severity(String label, String advice) {
        this.label = label;
        this.advice = advice;
    }

    public String getLabel() {
        return label;
    }

    public String getAdvice() {
        return advice;
    }
}
