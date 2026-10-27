package com.smartauto.model;

/**
 * درجة اليقين في التشخيص: كلما كانت الإجابات أدق وأكثر تحديدًا زاد اليقين.
 */
public enum Confidence {
    HIGH("عالٍ"),
    MEDIUM("متوسط"),
    LOW("منخفض — يلزم فحص للتأكد");

    private final String label;

    Confidence(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
