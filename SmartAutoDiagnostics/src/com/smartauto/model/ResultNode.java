package com.smartauto.model;

import java.util.Objects;

/**
 * عقدة نتيجة (ورقة في الشجرة): تحمل التشخيص النهائي.
 */
public class ResultNode extends DecisionNode {

    private final Diagnosis diagnosis;

    public ResultNode(Diagnosis diagnosis) {
        super(Objects.requireNonNull(diagnosis, "diagnosis").getCode());
        this.diagnosis = diagnosis;
    }

    public Diagnosis getDiagnosis() {
        return diagnosis;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
