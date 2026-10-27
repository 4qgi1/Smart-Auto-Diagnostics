package com.smartauto.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * نتيجة التشخيص النهائية: العطل المحتمل، شرحه، الحلول، والخطورة.
 * الكلاس غير قابل للتعديل (immutable) ويُبنى عبر {@link Builder}.
 */
public final class Diagnosis {

    private final String code;
    private final String title;
    private final String explanation;
    private final List<String> solutions;
    private final Severity severity;
    private final Confidence confidence;
    private final String tip;

    private Diagnosis(Builder b) {
        this.code = b.code;
        this.title = b.title;
        this.explanation = b.explanation;
        this.solutions = Collections.unmodifiableList(new ArrayList<>(b.solutions));
        this.severity = b.severity;
        this.confidence = b.confidence;
        this.tip = b.tip;
    }

    public static Builder builder(String code, String title) {
        return new Builder(code, title);
    }

    /** رمز فريد للعطل (مفيد للاختبارات والربط). */
    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getExplanation() {
        return explanation;
    }

    public List<String> getSolutions() {
        return solutions;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    /** ملاحظة إضافية اختيارية، أو null. */
    public String getTip() {
        return tip;
    }

    public static final class Builder {
        private final String code;
        private final String title;
        private String explanation;
        private final List<String> solutions = new ArrayList<>();
        private Severity severity = Severity.MEDIUM;
        private Confidence confidence = Confidence.MEDIUM;
        private String tip;

        private Builder(String code, String title) {
            this.code = Objects.requireNonNull(code, "code");
            this.title = Objects.requireNonNull(title, "title");
        }

        public Builder explanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public Builder solution(String solution) {
            this.solutions.add(Objects.requireNonNull(solution, "solution"));
            return this;
        }

        public Builder severity(Severity severity) {
            this.severity = Objects.requireNonNull(severity, "severity");
            return this;
        }

        public Builder confidence(Confidence confidence) {
            this.confidence = Objects.requireNonNull(confidence, "confidence");
            return this;
        }

        public Builder tip(String tip) {
            this.tip = tip;
            return this;
        }

        public Diagnosis build() {
            if (explanation == null || explanation.trim().isEmpty()) {
                throw new IllegalStateException("التشخيص " + code + " بدون شرح");
            }
            if (solutions.isEmpty()) {
                throw new IllegalStateException("التشخيص " + code + " بدون حلول");
            }
            return new Diagnosis(this);
        }
    }
}
