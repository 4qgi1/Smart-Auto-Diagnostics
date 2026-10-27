package com.smartauto.knowledge;

import com.smartauto.model.Diagnosis;
import com.smartauto.model.QuestionNode;
import com.smartauto.model.ResultNode;

/**
 * دوال مساعدة تجعل كتابة أشجار القرار مختصرة ومقروءة، وتصلح لأي مركبة.
 */
public final class Nodes {

    private Nodes() {
    }

    public static QuestionNode ask(String id, String text, String hint) {
        return new QuestionNode(id, text, hint);
    }

    public static ResultNode leaf(Diagnosis.Builder builder) {
        return new ResultNode(builder.build());
    }
}
