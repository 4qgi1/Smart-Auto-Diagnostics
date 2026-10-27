package com.smartauto.knowledge.tahoe;

import com.smartauto.knowledge.VehicleKnowledgeBase;
import com.smartauto.model.Symptom;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * قاعدة معرفة شفروليه تاهو بمحرك V8 (فئة 5.3 لتر).
 * كل عَرَض له كلاس مستقل، فإضافة عَرَض جديد = كلاس جديد + سطر واحد هنا.
 */
public class TahoeV8KnowledgeBase implements VehicleKnowledgeBase {

    private final List<Symptom> symptoms;

    public TahoeV8KnowledgeBase() {
        this.symptoms = Collections.unmodifiableList(Arrays.asList(
                MisfireTree.build(),
                OilPressureTree.build(),
                OverheatTree.build()));
    }

    @Override
    public String getVehicleName() {
        return "شفروليه تاهو (Chevrolet Tahoe) — محرك V8";
    }

    @Override
    public List<Symptom> getSymptoms() {
        return symptoms;
    }
}
