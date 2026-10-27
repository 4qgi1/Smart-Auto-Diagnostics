package com.smartauto.tools;

import com.smartauto.engine.TreeValidator;
import com.smartauto.knowledge.KnowledgeBaseRegistry;
import com.smartauto.knowledge.VehicleKnowledgeBase;
import com.smartauto.model.Choice;
import com.smartauto.model.DecisionNode;
import com.smartauto.model.QuestionNode;
import com.smartauto.model.ResultNode;
import com.smartauto.model.Symptom;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * أداة تولّد ملف Markdown فيه مخطط Mermaid لكل شجرة قرار، مباشرةً من الكود.
 * بهذا يبقى التوثيق مطابقًا للواقع دائمًا.
 *
 * التشغيل: java -cp out com.smartauto.tools.DocsGenerator docs/decision-trees.md
 */
public final class DocsGenerator {

    private static final String NL = "\n";

    private DocsGenerator() {
    }

    public static void main(String[] args) throws IOException {
        Path target = Paths.get(args.length > 0 ? args[0] : "docs/decision-trees.md");
        if (target.getParent() != null) {
            Files.createDirectories(target.getParent());
        }
        Files.write(target, generate().getBytes(StandardCharsets.UTF_8));
        new PrintStream(System.out, true, StandardCharsets.UTF_8)
                .println("تم إنشاء: " + target.toAbsolutePath());
    }

    public static String generate() {
        StringBuilder sb = new StringBuilder();
        sb.append("# أشجار القرار").append(NL).append(NL);
        sb.append("> ملف مُولَّد تلقائيًا من الكود بواسطة `DocsGenerator` — لا تعدّله يدويًا.").append(NL).append(NL);
        for (VehicleKnowledgeBase base : KnowledgeBaseRegistry.loadAll()) {
            sb.append("## ").append(base.getVehicleName()).append(NL).append(NL);
            for (Symptom symptom : base.getSymptoms()) {
                TreeValidator.Result stats = TreeValidator.validate(symptom);
                sb.append("### ").append(symptom.getTitle()).append(NL).append(NL);
                sb.append("- عدد الأسئلة: ").append(stats.getQuestionCount()).append(NL);
                sb.append("- عدد الأعطال (النتائج): ").append(stats.getResultCount()).append(NL);
                sb.append("- أطول مسار: ").append(stats.getMaxDepth()).append(" أسئلة").append(NL).append(NL);
                sb.append("```mermaid").append(NL);
                sb.append(toMermaid(symptom));
                sb.append("```").append(NL).append(NL);
            }
        }
        return sb.toString();
    }

    /** يحوّل شجرة عَرَض واحد إلى كود Mermaid. */
    public static String toMermaid(Symptom symptom) {
        Map<DecisionNode, String> ids = new IdentityHashMap<>();
        List<String> nodeLines = new ArrayList<>();
        List<String> edgeLines = new ArrayList<>();
        List<String> resultIds = new ArrayList<>();
        visit(symptom.getRoot(), ids, nodeLines, edgeLines, resultIds);

        StringBuilder sb = new StringBuilder();
        sb.append("flowchart TD").append(NL);
        for (String line : nodeLines) {
            sb.append("    ").append(line).append(NL);
        }
        for (String line : edgeLines) {
            sb.append("    ").append(line).append(NL);
        }
        sb.append("    classDef result fill:#e8f5e9,stroke:#2e7d32,color:#1b5e20;").append(NL);
        if (!resultIds.isEmpty()) {
            sb.append("    class ").append(String.join(",", resultIds)).append(" result;").append(NL);
        }
        return sb.toString();
    }

    private static String visit(DecisionNode node, Map<DecisionNode, String> ids,
                                List<String> nodeLines, List<String> edgeLines, List<String> resultIds) {
        String existing = ids.get(node);
        if (existing != null) {
            return existing;
        }
        String id = "n" + (ids.size() + 1);
        ids.put(node, id);

        if (node instanceof ResultNode) {
            String title = ((ResultNode) node).getDiagnosis().getTitle();
            nodeLines.add(id + "([\"" + clean(title) + "\"])");
            resultIds.add(id);
        } else {
            QuestionNode q = (QuestionNode) node;
            nodeLines.add(id + "[\"" + clean(q.getText()) + "\"]");
            for (Choice c : q.getChoices()) {
                String childId = visit(c.getNext(), ids, nodeLines, edgeLines, resultIds);
                edgeLines.add(id + " -->|\"" + clean(c.getLabel()) + "\"| " + childId);
            }
        }
        return id;
    }

    /** يمنع الرموز التي تكسر صياغة Mermaid. */
    private static String clean(String text) {
        return text.replace("\"", "'").replace("\n", " ");
    }
}
