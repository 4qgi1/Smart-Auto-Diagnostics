package com.smartauto.engine;

import com.smartauto.model.Choice;
import com.smartauto.model.DecisionNode;
import com.smartauto.model.Diagnosis;
import com.smartauto.model.QuestionNode;
import com.smartauto.model.ResultNode;
import com.smartauto.model.Symptom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * يتأكد أن شجرة القرار سليمة قبل استخدامها:
 * لا حلقات مغلقة، كل سؤال له خياران على الأقل، كل نتيجة لها شرح وحلول،
 * ولا يوجد رمزان متشابهان لعقدتين مختلفتين.
 */
public final class TreeValidator {

    private TreeValidator() {
    }

    /** نتيجة الفحص: الأخطاء (إن وجدت) + إحصائيات عن الشجرة. */
    public static final class Result {
        private final List<String> errors;
        private final int questionCount;
        private final int resultCount;
        private final int maxDepth;

        private Result(List<String> errors, int questionCount, int resultCount, int maxDepth) {
            this.errors = Collections.unmodifiableList(errors);
            this.questionCount = questionCount;
            this.resultCount = resultCount;
            this.maxDepth = maxDepth;
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public List<String> getErrors() {
            return errors;
        }

        /** عدد الأسئلة الفريدة في الشجرة. */
        public int getQuestionCount() {
            return questionCount;
        }

        /** عدد النتائج (الأعطال) الفريدة في الشجرة. */
        public int getResultCount() {
            return resultCount;
        }

        /** أكبر عدد أسئلة قد يجيب عنها المستخدم في مسار واحد. */
        public int getMaxDepth() {
            return maxDepth;
        }
    }

    public static Result validate(Symptom symptom) {
        Walker walker = new Walker();
        int depth = walker.walk(symptom.getRoot());
        return new Result(walker.errors, walker.questionCount, walker.resultCount, depth);
    }

    private static final class Walker {
        private final List<String> errors = new ArrayList<>();
        private final Set<DecisionNode> inProgress =
                Collections.newSetFromMap(new IdentityHashMap<DecisionNode, Boolean>());
        private final Map<DecisionNode, Integer> depthMemo = new IdentityHashMap<>();
        private final Map<String, DecisionNode> idsSeen = new HashMap<>();
        private final Set<String> reportedDuplicates = new HashSet<>();
        private int questionCount;
        private int resultCount;

        int walk(DecisionNode node) {
            Integer known = depthMemo.get(node);
            if (known != null) {
                return known;
            }
            if (!inProgress.add(node)) {
                errors.add("حلقة مغلقة عند العقدة: " + node.getId());
                return 0;
            }
            checkId(node);

            int depth;
            if (node instanceof ResultNode) {
                resultCount++;
                checkDiagnosis((ResultNode) node);
                depth = 0;
            } else if (node instanceof QuestionNode) {
                questionCount++;
                depth = 1 + walkQuestion((QuestionNode) node);
            } else {
                errors.add("نوع عقدة غير معروف: " + node.getId());
                depth = 0;
            }

            inProgress.remove(node);
            depthMemo.put(node, depth);
            return depth;
        }

        private int walkQuestion(QuestionNode q) {
            if (q.getText().trim().isEmpty()) {
                errors.add("السؤال " + q.getId() + " نصه فارغ");
            }
            List<Choice> choices = q.getChoices();
            if (choices.size() < 2) {
                errors.add("السؤال " + q.getId() + " عنده أقل من خيارين");
            }
            Set<String> labels = new HashSet<>();
            int maxChild = 0;
            for (Choice c : choices) {
                if (c.getLabel().trim().isEmpty()) {
                    errors.add("السؤال " + q.getId() + " فيه خيار نصه فارغ");
                } else if (!labels.add(c.getLabel())) {
                    errors.add("السؤال " + q.getId() + " فيه خياران متطابقان: " + c.getLabel());
                }
                maxChild = Math.max(maxChild, walk(c.getNext()));
            }
            return maxChild;
        }

        private void checkDiagnosis(ResultNode node) {
            Diagnosis d = node.getDiagnosis();
            if (d.getTitle().trim().isEmpty()) {
                errors.add("النتيجة " + d.getCode() + " بدون عنوان");
            }
            if (d.getSolutions().isEmpty()) {
                errors.add("النتيجة " + d.getCode() + " بدون حلول");
            }
        }

        private void checkId(DecisionNode node) {
            DecisionNode other = idsSeen.get(node.getId());
            if (other == null) {
                idsSeen.put(node.getId(), node);
            } else if (other != node && reportedDuplicates.add(node.getId())) {
                errors.add("رمز مكرر لعقدتين مختلفتين: " + node.getId());
            }
        }
    }
}
