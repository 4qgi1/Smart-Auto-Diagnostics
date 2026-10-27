package com.smartauto.model;

import java.util.Objects;

/**
 * عقدة في شجرة القرار. إما سؤال ({@link QuestionNode}) أو نتيجة ({@link ResultNode}).
 * هذا الكلاس المجرّد يمثل الـ Abstraction والـ Polymorphism في المشروع.
 */
public abstract class DecisionNode {

    private final String id;

    protected DecisionNode(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public String getId() {
        return id;
    }

    /** هل هذه العقدة نهاية الشجرة (نتيجة)؟ */
    public abstract boolean isLeaf();
}
