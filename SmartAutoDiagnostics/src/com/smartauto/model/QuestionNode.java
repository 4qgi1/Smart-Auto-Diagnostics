package com.smartauto.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * عقدة سؤال: نص السؤال، توضيح اختياري، وخيارات الإجابة.
 * كل خيار يقود إلى عقدة أخرى، وبذلك تتكوّن شجرة القرار.
 */
public class QuestionNode extends DecisionNode {

    private final String text;
    private final String hint;
    private final List<Choice> choices = new ArrayList<>();

    public QuestionNode(String id, String text, String hint) {
        super(id);
        this.text = Objects.requireNonNull(text, "text");
        this.hint = hint;
    }

    /** يضيف خيار إجابة ويرجع نفس العقدة ليسهل التسلسل (fluent). */
    public QuestionNode choice(String label, DecisionNode next) {
        choices.add(new Choice(label, next));
        return this;
    }

    public String getText() {
        return text;
    }

    /** توضيح يساعد المستخدم على الإجابة، أو null. */
    public String getHint() {
        return hint;
    }

    public List<Choice> getChoices() {
        return Collections.unmodifiableList(choices);
    }

    @Override
    public boolean isLeaf() {
        return false;
    }
}
