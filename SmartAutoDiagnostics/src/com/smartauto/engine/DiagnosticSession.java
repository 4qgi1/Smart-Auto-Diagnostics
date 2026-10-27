package com.smartauto.engine;

import com.smartauto.knowledge.VehicleKnowledgeBase;
import com.smartauto.model.Choice;
import com.smartauto.model.DecisionNode;
import com.smartauto.model.Diagnosis;
import com.smartauto.model.QuestionNode;
import com.smartauto.model.ResultNode;
import com.smartauto.model.Symptom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * جلسة تشخيص واحدة: تتحرك داخل شجرة القرار حسب إجابات المستخدم،
 * وتحتفظ بمسار الإجابات لإظهاره في التقرير ولدعم الرجوع للسؤال السابق.
 * لا تعرف شيئًا عن الواجهة (طباعة أو إدخال) فيمكن اختبارها بسهولة.
 */
public class DiagnosticSession {

    private final VehicleKnowledgeBase vehicle;
    private final Symptom symptom;
    private final List<AnswerStep> path = new ArrayList<>();
    private DecisionNode current;

    public DiagnosticSession(VehicleKnowledgeBase vehicle, Symptom symptom) {
        this.vehicle = Objects.requireNonNull(vehicle, "vehicle");
        this.symptom = Objects.requireNonNull(symptom, "symptom");
        this.current = symptom.getRoot();
    }

    public VehicleKnowledgeBase getVehicle() {
        return vehicle;
    }

    public Symptom getSymptom() {
        return symptom;
    }

    public boolean isFinished() {
        return current.isLeaf();
    }

    /** السؤال الحالي. يرمي استثناء إذا انتهت الجلسة. */
    public QuestionNode getCurrentQuestion() {
        if (isFinished()) {
            throw new IllegalStateException("انتهى التشخيص، لا يوجد سؤال حالي");
        }
        return (QuestionNode) current;
    }

    /**
     * يسجّل إجابة المستخدم وينتقل للعقدة التالية.
     *
     * @param choiceNumber رقم الخيار ابتداءً من 1 (كما يراه المستخدم)
     */
    public void answer(int choiceNumber) {
        QuestionNode question = getCurrentQuestion();
        List<Choice> choices = question.getChoices();
        if (choiceNumber < 1 || choiceNumber > choices.size()) {
            throw new IllegalArgumentException(
                    "رقم الخيار " + choiceNumber + " خارج النطاق (1 إلى " + choices.size() + ")");
        }
        Choice chosen = choices.get(choiceNumber - 1);
        path.add(new AnswerStep(question, chosen));
        current = chosen.getNext();
    }

    public boolean canGoBack() {
        return !path.isEmpty();
    }

    /** يرجع للسؤال السابق ويلغي آخر إجابة. يرجع false إن لم توجد إجابات سابقة. */
    public boolean goBack() {
        if (path.isEmpty()) {
            return false;
        }
        AnswerStep last = path.remove(path.size() - 1);
        current = last.getQuestion();
        return true;
    }

    /** التشخيص النهائي. يرمي استثناء إذا لم تنتهِ الجلسة بعد. */
    public Diagnosis getDiagnosis() {
        if (!isFinished()) {
            throw new IllegalStateException("التشخيص لم يكتمل بعد");
        }
        return ((ResultNode) current).getDiagnosis();
    }

    /** مسار الأسئلة والإجابات حتى الآن. */
    public List<AnswerStep> getPath() {
        return Collections.unmodifiableList(path);
    }
}
