package com.smartauto.knowledge;

import com.smartauto.knowledge.tahoe.TahoeV8KnowledgeBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * سجل قواعد المعرفة المتاحة. أضف هنا أي مركبة جديدة.
 */
public final class KnowledgeBaseRegistry {

    private KnowledgeBaseRegistry() {
    }

    public static List<VehicleKnowledgeBase> loadAll() {
        List<VehicleKnowledgeBase> all = new ArrayList<>();
        all.add(new TahoeV8KnowledgeBase());
        // all.add(new YourNewCarKnowledgeBase());
        return Collections.unmodifiableList(all);
    }
}
