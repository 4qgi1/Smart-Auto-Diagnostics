package com.smartauto.model;

import java.util.Objects;

/**
 * خيار إجابة على سؤال: نص يظهر للمستخدم + العقدة التالية التي يقود إليها.
 */
public final class Choice {

    private final String label;
    private final DecisionNode next;

    public Choice(String label, DecisionNode next) {
        this.label = Objects.requireNonNull(label, "label");
        this.next = Objects.requireNonNull(next, "next");
    }

    public String getLabel() {
        return label;
    }

    public DecisionNode getNext() {
        return next;
    }
}
