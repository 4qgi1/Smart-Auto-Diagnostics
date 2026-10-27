package com.smartauto.knowledge;

import com.smartauto.model.Symptom;

import java.util.List;

/**
 * قاعدة معرفة لمركبة معيّنة: اسمها + الأعراض التي نستطيع تشخيصها.
 * لإضافة سيارة جديدة يكفي إنشاء كلاس جديد ينفّذ هذه الواجهة
 * وتسجيله في {@link KnowledgeBaseRegistry} دون تعديل بقية النظام.
 */
public interface VehicleKnowledgeBase {

    /** اسم المركبة كما يظهر للمستخدم وفي التقرير. */
    String getVehicleName();

    /** الأعراض المدعومة لهذه المركبة. */
    List<Symptom> getSymptoms();
}
