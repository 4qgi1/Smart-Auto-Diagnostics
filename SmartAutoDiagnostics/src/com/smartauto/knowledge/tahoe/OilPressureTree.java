package com.smartauto.knowledge.tahoe;

import static com.smartauto.knowledge.Nodes.ask;
import static com.smartauto.knowledge.Nodes.leaf;

import com.smartauto.model.Confidence;
import com.smartauto.model.Diagnosis;
import com.smartauto.model.QuestionNode;
import com.smartauto.model.ResultNode;
import com.smartauto.model.Severity;
import com.smartauto.model.Symptom;

/**
 * شجرة عَرَض «طقطقة مع انخفاض ضغط الزيت».
 */
final class OilPressureTree {

    private OilPressureTree() {
    }

    static Symptom build() {

        // ───────────── النتائج ─────────────

        ResultNode overfill = leaf(Diagnosis.builder("OIL_OVERFILL",
                "زيت زائد عن العلامة العليا")
                .explanation("الزيت الزائد يلامس الكرنك فيتحول إلى رغوة ويقل ضغطه، فيصدر المحرك أصواتًا وقد "
                        + "تظهر لمبة الضغط. وإذا ارتفع المستوى دون أن تضيف زيتًا فقد يكون الزيت مختلطًا "
                        + "بوقود أو بسائل تبريد.")
                .solution("اسحب الزائد حتى يصبح المستوى بين العلامتين.")
                .solution("افحص الزيت: رائحة بنزين أو لون حليبي علامة اختلاط، وفي هذه الحالة لا تشغّل المحرك "
                        + "وافحصه في ورشة.")
                .solution("أعد فحص الضغط والصوت بعد تصحيح المستوى.")
                .severity(Severity.MEDIUM)
                .confidence(Confidence.MEDIUM));

        ResultNode leak = leaf(Diagnosis.builder("OIL_LEAK",
                "نقص الزيت بسبب تسرب")
                .explanation("انخفاض مستوى الزيت يقلل الضغط ويحرم رافعات الصمامات من التزييت فتظهر الطقطقة. "
                        + "التسرب يأتي عادةً من جوان الكارتير أو غطاء الصمامات أو الفلتر أو الجوان الخلفي للكرنك.")
                .solution("قبل أي تشغيل أضف زيتًا بالمواصفة الصحيحة حتى العلامة العليا.")
                .solution("حدّد مصدر التسرب (ويُفضّل تنظيف المنطقة ثم تتبّع الأثر بعد تشغيل قصير).")
                .solution("أصلح المصدر: تبديل الجوان أو الفلتر أو الصمام المتسرب.")
                .solution("إذا بقيت الطقطقة بعد تعبئة الزيت فاطلب فحص الرافعات والكامة.")
                .severity(Severity.HIGH)
                .confidence(Confidence.MEDIUM)
                .tip("لا تقد السيارة والمسطرة جافة؛ المحرك قد يتضرر خلال دقائق."));

        ResultNode consumption = leaf(Diagnosis.builder("OIL_CONSUMPTION",
                "استهلاك زيت داخلي مرتفع")
                .explanation("الزيت ينقص دون تسرب ظاهر لأنه يُحرق داخل الأسطوانات (وقد يظهر دخان أزرق). "
                        + "من أسباب ذلك المعروفة في محركات V8 ذات AFM: تآكل حلقات المكابس، أو صمام تهوية "
                        + "الكرنك (PCV)، أو أضرار مرتبطة بنظام AFM.")
                .solution("أضف الزيت حتى العلامة العليا وقِس الاستهلاك بين تغييرين متتاليين.")
                .solution("بدّل صمام PCV وافحص مجرى التهوية.")
                .solution("اطلب اختبار ضغط الأسطوانات (Compression) وتسرب الأسطوانات (Leak-down).")
                .solution("إن ثبت تلف الحلقات فالإصلاح كبير (تأهيل المحرك)، وقد يُدرج إلغاء AFM ضمنه.")
                .severity(Severity.HIGH)
                .confidence(Confidence.MEDIUM));

        ResultNode rodBearing = leaf(Diagnosis.builder("ROD_BEARING",
                "طرق سفلي ثقيل — احتمال تآكل كراسي الكرنك أو الأذرع (Rod Knock)")
                .explanation("الطرق العميق من أسفل المحرك الذي يزيد مع الدوران مع ضعف الضغط من علامات تآكل "
                        + "الكراسي. الاستمرار في التشغيل قد يحوّل العطل إلى كسر يدمّر المحرك بالكامل.")
                .solution("أوقف المحرك الآن ولا تعد تشغيله.")
                .solution("انقل السيارة بسطحة (ونش) إلى ورشة متخصصة.")
                .solution("اطلب فحص الكراسي (بنزع الكارتير) وفحص الزيت والفلتر بحثًا عن برادة معدنية.")
                .solution("حسب الحالة: تبديل الكراسي والأذرع، أو تأهيل المحرك، أو تبديله.")
                .severity(Severity.CRITICAL)
                .confidence(Confidence.MEDIUM)
                .tip("كل دقيقة تشغيل إضافية ترفع تكلفة الإصلاح."));

        ResultNode realLowPressure = leaf(Diagnosis.builder("REAL_LOW_PRESSURE",
                "ضغط زيت منخفض فعليًا (مضخة أو كراسي أو زيت مخفف)")
                .explanation("مستوى الزيت سليم لكن الضغط يبقى ضعيفًا، فالمشكلة غالبًا في مضخة الزيت أو انسداد "
                        + "المصفاة أو تآكل الكراسي أو تخفيف الزيت بالوقود. وانخفاض الضغط يفسّر الطقطقة لأن "
                        + "رافعات الصمامات لا تحصل على تزييت كافٍ.")
                .solution("لا تشغّل المحرك إلا للضرورة القصوى.")
                .solution("اطلب قياس الضغط بمانومتر ميكانيكي للتأكد من القراءة الفعلية.")
                .solution("افحص الزيت من رائحة الوقود أو برادة المعدن، وبدّل الزيت والفلتر.")
                .solution("حسب النتيجة: تبديل المضخة أو تنظيف/تبديل المصفاة أو فحص الكراسي.")
                .severity(Severity.CRITICAL)
                .confidence(Confidence.MEDIUM)
                .tip("لمبة ضغط الزيت الحمراء الثابتة تعني: أوقف المحرك فورًا."));

        ResultNode oilService = leaf(Diagnosis.builder("OIL_SERVICE",
                "زيت قديم أو لزوجة غير مناسبة")
                .explanation("الزيت القديم أو المخالف للمواصفة يفقد قدرته على تكوين الضغط عند الحرارة "
                        + "والهدوء، فيظهر انخفاض مؤقت وطقطقة في الرافعات.")
                .solution("بدّل الزيت والفلتر بالمواصفة المذكورة في دليل المالك "
                        + "(لمحرك 5.3 غالبًا 0W-20 بمعيار dexos أو 5W-30 للموديلات الأقدم، فراجع الدليل).")
                .solution("لاحظ حالة الزيت: أسود سميك أو فيه رواسب يحتاج تنظيفًا للمحرك.")
                .solution("إذا استمرت الطقطقة بعد الزيت الجديد فاطلب فحص رافعات AFM والكامة.")
                .severity(Severity.MEDIUM)
                .confidence(Confidence.MEDIUM));

        ResultNode wornBearingsPump = leaf(Diagnosis.builder("WORN_BEARINGS_PUMP",
                "بداية تآكل في الكراسي أو ضعف في مضخة الزيت")
                .explanation("الضغط ينخفض عند الهدوء والحرارة رغم أن الزيت جديد ومناسب، وهذا يشير إلى فراغات "
                        + "زائدة في الكراسي أو مضخة بدأت تضعف.")
                .solution("قِس الضغط بمانومتر ميكانيكي عند الهدوء والمحرك حار، وعند الدوران العالي.")
                .solution("قارن القراءة بالمواصفة في دليل الصيانة.")
                .solution("إذا تأكد الانخفاض: افحص مضخة الزيت وكراسي المحرك وبدّل المتضرر.")
                .solution("تجنّب الأحمال العالية والقيادة الطويلة حتى يتم الفحص.")
                .severity(Severity.HIGH)
                .confidence(Confidence.LOW));

        ResultNode sensor = leaf(Diagnosis.builder("OIL_SENSOR",
                "حساس ضغط الزيت أو وصلته معيب")
                .explanation("قفزة القراءة المفاجئة أو ظهور التحذير لحظيًا مع صوت محرك طبيعي من أشهر علامات "
                        + "عطل حساس الضغط أو توصيلاته، وهو عطل شائع وتكلفته منخفضة نسبيًا.")
                .solution("افحص قابس الحساس والسلك، ثم بدّل حساس ضغط الزيت.")
                .solution("إن أمكن أكّد بقياس ميكانيكي قبل استبعاد مشكلة ضغط حقيقية.")
                .solution("إذا بقيت الطقطقة بعد تبديل الحساس فهي مشكلة منفصلة (غالبًا رافعة) وتحتاج فحصًا.")
                .severity(Severity.MEDIUM)
                .confidence(Confidence.MEDIUM));

        // ───────────── الأسئلة (من الأسفل للأعلى) ─────────────

        QuestionNode oilAge = ask("O_OIL_AGE",
                "كيف وضع تبديل الزيت والفلتر عندك؟",
                null)
                .choice("متأخر عن الموعد أو لا أعرف نوع الزيت المستخدم", oilService)
                .choice("منتظم وبالمواصفة الصحيحة", wornBearingsPump);

        QuestionNode top = ask("O_TOP",
                "كيف يظهر انخفاض الضغط؟",
                null)
                .choice("اللمبة ثابتة أو القراءة منخفضة باستمرار", realLowPressure)
                .choice("ينخفض فقط عند الهدوء (الرينجي) والمحرك حار", oilAge)
                .choice("يقفز العداد فجأة أو يظهر التحذير لحظيًا والمحرك يبدو طبيعيًا", sensor);

        QuestionNode low = ask("O_LOW",
                "هل ترى بقع زيت تحت السيارة أو على المحرك؟",
                null)
                .choice("نعم، في أثر تسرب", leak)
                .choice("لا، ما في تسرب (وقد يوجد دخان أزرق من العادم)", consumption);

        QuestionNode normal = ask("O_NORMAL",
                "من أين يأتي صوت الطقطقة تقريبًا؟",
                "اطلب من شخص يسمع الصوت وأنت تشغّل المحرك، أو استخدم سماعة ميكانيكي إن توفرت.")
                .choice("من أعلى المحرك (جهة غطاء الصمامات)، خفيف وسريع", top)
                .choice("من أسفل المحرك، طرق ثقيل يزيد مع الدوران", rodBearing)
                .choice("لا أعرف مصدره بدقة", top);

        QuestionNode root = ask("O_ROOT",
                "أوقف المحرك وانتظر 5 دقائق على أرض مستوية ثم افحص مستوى الزيت بالمسطرة. كيف المستوى؟",
                "إذا كانت لمبة ضغط الزيت حمراء ثابتة فأوقف المحرك فورًا قبل أي فحص؛ "
                        + "التشغيل بدون ضغط يتلف المحرك بسرعة.")
                .choice("أقل من العلامة الدنيا أو المسطرة شبه جافة", low)
                .choice("طبيعي بين العلامتين", normal)
                .choice("أعلى من العلامة العليا", overfill);

        return new Symptom("OIL_TICK", "طقطقة مع انخفاض ضغط الزيت",
                "صوت طقطقة أو طرق من المحرك مع لمبة ضغط الزيت أو قراءة منخفضة في العداد.", root);
    }
}
