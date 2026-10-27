package com.smartauto.engine;

import com.smartauto.model.Choice;
import com.smartauto.model.QuestionNode;

import java.util.Objects;

/**
 * خطوة واحدة في مسار التحقيق: السؤال الذي طُرح والخيار الذي اختاره المستخدم.
 */
public final class AnswerStep {

    private final QuestionNode question;
    private final Choice choice;

    public AnswerStep(QuestionNode question, Choice choice) {
        this.question = Objects.requireNonNull(question, "question");
        this.choice = Objects.requireNonNull(choice, "choice");
    }

    public QuestionNode getQuestion() {
        return question;
    }

    public Choice getChoice() {
        return choice;
    }

    public String getQuestionText() {
        return question.getText();
    }

    public String getAnswerLabel() {
        return choice.getLabel();
    }
}
