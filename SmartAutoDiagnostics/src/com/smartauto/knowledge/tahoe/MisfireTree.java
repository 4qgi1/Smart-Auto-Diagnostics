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
 * شجرة عَرَض «تفتفة في المحرك».
 * تُبنى من الأوراق (النتائج) إلى الجذر، ثم تُربط الأسئلة ببعضها.
 */
final class MisfireTree {

    private MisfireTree() {
    }

    static Symptom build() {

        // ───────────── النتائج ─────────────

        ResultNode afmLifter = leaf(Diagnosis.builder("AFM_LIFTER",
                "انهيار رافعة (Lifter) في نظام AFM")
                .explanation("نظام AFM يوقف أربع أسطوانات (1 و4 و6 و7) لتوفير الوقود، ويعتمد على رافعات خاصة "
                        + "تعمل بضغط الزيت. هذه الرافعات قد تنهار أو تعلق مع الوقت، فتتوقف الأسطوانة عن فتح "
                        + "الصمام وتظهر تفتفة مع صوت تكتكة، وقد تكون أكثر من رافعة متضررة.")
                .solution("اطلب فحص الرافعات وعمود الكامة بفك غطاء الصمامات للتأكد من الرافعة المنهارة "
                        + "وحالة فصوص الكامة.")
                .solution("الحل الجذري (1): استبدال الرافعات المتضررة (ويُفضّل رافعات AFM كلها) "
                        + "مع استبدال الكامة إذا ظهر عليها تآكل.")
                .solution("الحل الجذري (2): تركيب طقم إلغاء AFM (رافعات عادية + كامة مناسبة) "
                        + "مع تعديل برمجة كمبيوتر السيارة (ECU Tune) لإيقاف النظام نهائيًا.")
                .solution("قبل أي تعديل تأكد من تأثيره على الضمان ومن اشتراطات الفحص الفني الدوري.")
                .severity(Severity.HIGH)
                .confidence(Confidence.HIGH)
                .tip("كل ما تأخرت في الإصلاح زاد احتمال تلف الكامة وارتفعت التكلفة؛ "
                        + "ولا تقد السيارة لمسافات طويلة وهي بهذه الحالة."));

        ResultNode coil = leaf(Diagnosis.builder("COIL",
                "تلف الكويل (ملف الشرارة) في الأسطوانة المعنية")
                .explanation("في كل أسطوانة كويل مستقل يرفع الجهد للشمعة. عندما ينتقل رقم الأسطوانة في كود "
                        + "الحريق مع الكويل بعد التبديل فهذا يثبت أن الكويل نفسه هو سبب ضعف أو انقطاع الشرارة.")
                .solution("استبدل الكويل التالف بآخر أصلي أو من جودة معروفة.")
                .solution("افحص شمعة نفس الأسطوانة وبدّلها معه؛ فالكويل الضعيف قد يتلف الشمعة.")
                .solution("إذا كانت بقية الكويلات قديمة وعليها كيلومترات عالية فكّر في تبديلها دفعة واحدة "
                        + "لتفادي تكرار العطل.")
                .solution("بعد الإصلاح امسح الأكواد وجرّب السيارة للتأكد من اختفاء الحريق.")
                .severity(Severity.MEDIUM)
                .confidence(Confidence.HIGH)
                .tip("الحريق المستمر قد يتلف الكتلايزر (المحول الحفّاز)، فلا تؤجّل الإصلاح."));

        ResultNode plugInjectorMech = leaf(Diagnosis.builder("PLUG_INJECTOR_MECH",
                "عطل في الشمعة أو البخاخ أو ضعف ضغط داخل الأسطوانة")
                .explanation("الكود بقي في نفس الأسطوانة بعد تبديل الكويل، فالكويل غالبًا سليم. الأسباب الأقرب: "
                        + "شمعة تالفة أو بخاخ وقود لا يعمل، وإن لم يظهر ذلك فيُحتمل وجود مشكلة ميكانيكية "
                        + "(صمام أو رافعة أو حلقات).")
                .solution("افحص الشمعة وبدّلها، وتأكد من المسافة بين الأقطاب حسب المواصفة.")
                .solution("اختبر البخاخ بتبديله مع بخاخ أسطوانة أخرى أو بقياس مقاومته وتشغيله بالجهاز.")
                .solution("اطلب فحص ضغط الأسطوانة (Compression Test) لاستبعاد العطل الميكانيكي.")
                .solution("إذا كانت الأسطوانة من أسطوانات AFM (1 و4 و6 و7) فاطلب فحص الرافعة أيضًا "
                        + "حتى لو لم تسمع تكتكة واضحة.")
                .severity(Severity.MEDIUM)
                .confidence(Confidence.MEDIUM));

        ResultNode coilTestNeeded = leaf(Diagnosis.builder("COIL_TEST_NEEDED",
                "الأرجح كويل أو شمعة تالفة — يلزم اختبار التبديل للتأكيد")
                .explanation("الكود يحدد الأسطوانة المعطّلة لكنه لا يحدد القطعة. أكثر الأسباب شيوعًا في هذه "
                        + "الحالة هي الكويل ثم الشمعة، ويمكن تأكيد ذلك باختبار بسيط قبل شراء أي قطعة.")
                .solution("افصل كويل الأسطوانة المعطّلة (يُفك غالبًا ببرغي واحد وقابس كهرباء) وبدّله مع "
                        + "كويل أسطوانة سليمة.")
                .solution("امسح الأكواد وشغّل السيارة ثم افحص الأكواد من جديد.")
                .solution("إذا انتقل الكود مع الكويل فاستبدله. وإذا بقي في نفس الأسطوانة فافحص الشمعة والبخاخ.")
                .solution("بعد الاختبار أعد التشخيص واختر «نعم» في سؤال تبديل الكويل.")
                .severity(Severity.MEDIUM)
                .confidence(Confidence.LOW));

        ResultNode coldStart = leaf(Diagnosis.builder("COLD_START",
                "بخاخات متسخة أو تسرب هواء بسيط يظهر على البارد")
                .explanation("التفتفة التي تخف بعد أن يسخن المحرك تشير غالبًا إلى مشكلة في تذرية الوقود أو في "
                        + "خليط الهواء والوقود وقت البرودة: بخاخات متسخة، أو تسرب فراغ صغير، أو ترسبات كربون.")
                .solution("نظّف البخاخات (منظّف يُضاف للبنزين أو تنظيف احترافي بالجهاز).")
                .solution("افحص خراطيم الهواء وخرطوم PCV ومجرى دخول الهواء بحثًا عن تشققات.")
                .solution("نظّف الثروتل بودي (Throttle Body) وحساس MAF.")
                .solution("إذا استمر العطل فاطلب قراءة بيانات الوقود الحية (Fuel Trims) بجهاز الفحص.")
                .severity(Severity.LOW)
                .confidence(Confidence.LOW));

        ResultNode plugsLoad = leaf(Diagnosis.builder("PLUGS_LOAD",
                "شمعات اشتعال متآكلة أو كويلات ضعيفة تحت الحمل")
                .explanation("تحت الحمل (تسارع أو حمولة) يرتفع ضغط الأسطوانة وتحتاج الشمعة إلى جهد أعلى؛ "
                        + "فإذا كانت متآكلة أو كان الكويل ضعيفًا تنقطع الشرارة وتظهر تفتفة عشوائية.")
                .solution("بدّل الشمعات الثمانية معًا بالنوع المعتمد في دليل المالك.")
                .solution("افحص الكويلات والأغطية المطاطية (Boots) من تشقق أو أثر شرارة.")
                .solution("اربط الشمعات بعزم الربط الصحيح ولا تبالغ في الشد.")
                .solution("بعد التبديل امسح الأكواد وجرّب السيارة تحت حمل.")
                .severity(Severity.MEDIUM)
                .confidence(Confidence.MEDIUM));

        ResultNode fuelAirGeneral = leaf(Diagnosis.builder("FUEL_AIR_GENERAL",
                "خلل عام في الوقود أو الهواء")
                .explanation("لا توجد تكتكة ولا كود محدد والتفتفة تظهر بشكل عام؛ فيُحتمل نقص وصول الوقود "
                        + "(فلتر أو مضخة أو ضغط ضعيف) أو دخول هواء غير محسوب.")
                .solution("قِس ضغط الوقود وقارنه بالمواصفة.")
                .solution("افحص فلتر الوقود (حسب الموديل قد يكون داخل الخزان) ومضخة الوقود.")
                .solution("افحص فلتر الهواء وحساس MAF وخراطيم المدخل.")
                .solution("اطلب فحصًا بالكمبيوتر وقراءة بيانات الوقود الحية (Fuel Trims).")
                .severity(Severity.MEDIUM)
                .confidence(Confidence.LOW));

        ResultNode vacuumLeak = leaf(Diagnosis.builder("VACUUM_LEAK",
                "تسرب هواء (فراغ) بعد حساس الهواء")
                .explanation("كود الخليط الفقير مع صفير أو تحسّن الاهتزاز عند رفع الدوران من علامات دخول هواء "
                        + "غير مقاس إلى المحرك، فيقل الوقود بالنسبة للهواء.")
                .solution("افحص خراطيم المدخل وخرطوم PCV بحثًا عن تشقق أو انفصال.")
                .solution("افحص جوان منفولد السحب ووصلات خراطيم الفراغ.")
                .solution("اطلب اختبار دخان (Smoke Test) لتحديد مكان التسرب بدقة.")
                .severity(Severity.LOW)
                .confidence(Confidence.MEDIUM));

        ResultNode mafDirty = leaf(Diagnosis.builder("MAF_DIRTY",
                "حساس تدفق الهواء (MAF) متسخ أو فلتر الهواء مسدود")
                .explanation("الحساس المتسخ يقرأ كمية هواء أقل من الحقيقية فيُحسب الوقود بشكل خاطئ، "
                        + "فيظهر كود خليط فقير مع اهتزاز.")
                .solution("نظّف الحساس بمنظّف مخصص لحساسات MAF فقط، ولا تلمس السلك الداخلي.")
                .solution("بدّل فلتر الهواء إن كان متسخًا.")
                .solution("امسح الأكواد وراقب هل تعود.")
                .severity(Severity.LOW)
                .confidence(Confidence.MEDIUM));

        ResultNode fuelSystem = leaf(Diagnosis.builder("FUEL_SYSTEM",
                "ضعف في منظومة الوقود (ضغط أو مضخة أو فلتر)")
                .explanation("الكود يستمر رغم أن الهواء سليم، فالأقرب أن الوقود الواصل غير كافٍ "
                        + "أو أن حساسات الأكسجين تعطي قراءة خاطئة.")
                .solution("قِس ضغط الوقود وقارنه بالمواصفة.")
                .solution("افحص مضخة الوقود والفلتر.")
                .solution("افحص البخاخات من انسداد أو تسرب.")
                .solution("افحص حساسات الأكسجين وقراءاتها الحية.")
                .severity(Severity.MEDIUM)
                .confidence(Confidence.LOW));

        ResultNode flashingCel = leaf(Diagnosis.builder("FLASHING_CEL",
                "حريق شديد في المحرك (لمبة الفحص تومض)")
                .explanation("وميض لمبة فحص المحرك يعني حريقًا شديدًا في الأسطوانات؛ الوقود غير المحترق يصل "
                        + "إلى العادم وقد يتلف الكتلايزر (المحول الحفّاز) بسرعة.")
                .solution("خفّف السرعة والحمل فورًا واركن في مكان آمن، وتجنّب القيادة لمسافات طويلة.")
                .solution("اقرأ الأكواد بجهاز OBD2، ثم أعد التشخيص من أول سؤال واختر «نعم، فحصت».")
                .solution("إذا كان الاهتزاز شديدًا فاستخدم سطحة (ونش) إلى الورشة.")
                .severity(Severity.HIGH)
                .confidence(Confidence.HIGH));

        ResultNode scanNeeded = leaf(Diagnosis.builder("SCAN_NEEDED",
                "لا بد من قراءة الأكواد أولًا")
                .explanation("لمبة الفحص مضاءة، وهذا يعني أن كمبيوتر السيارة سجّل عطلًا؛ "
                        + "والتشخيص الدقيق يعتمد على معرفة الكود قبل أي تخمين أو شراء قطع.")
                .solution("افحص السيارة بجهاز OBD2 (يتوفر بأسعار بسيطة ويتصل بالجوال، وتفحصه كثير من الورش).")
                .solution("سجّل الأكواد (مثل P0301 أو P0300 أو P0171).")
                .solution("ارجع للبرنامج واختر «نعم، فحصت» لتتابع التشخيص بدقة.")
                .severity(Severity.LOW)
                .confidence(Confidence.LOW));

        ResultNode plugsOld = leaf(Diagnosis.builder("PLUGS_OLD",
                "شمعات الاشتعال انتهى عمرها")
                .explanation("الشمعات القديمة تتآكل أقطابها فتحتاج جهدًا أعلى وتضعف الشرارة، "
                        + "فتظهر تفتفة قد لا يُحفظ لها كود في بدايتها.")
                .solution("بدّل الشمعات الثمانية معًا بالنوع المعتمد في دليل المالك.")
                .solution("افحص الكويلات والأغطية المطاطية أثناء الفك.")
                .solution("راقب السيارة بعد التبديل، فإن عاد الاهتزاز أعد التشخيص.")
                .severity(Severity.LOW)
                .confidence(Confidence.MEDIUM));

        // ───────────── الأسئلة (من الأسفل للأعلى) ─────────────

        QuestionNode randomTick = ask("M_RANDOM_TICK",
                "هل تسمع صوت تكتكة (طقطقة) منتظمة من المحرك؟",
                "الصوت يشبه ساعة تتكتك وتزداد سرعته مع زيادة الدوران، ويكون أوضح عند التشغيل أو الهدوء.")
                .choice("نعم، في تكتكة واضحة", afmLifter)
                .choice("لا، ما في صوت", fuelAirGeneral);

        QuestionNode coilSwap = ask("M_COIL_SWAP",
                "هل جرّبت تبديل الكويل (ملف الشرارة) بين الأسطوانة المعطّلة وأسطوانة سليمة ثم أعدت فحص الكود؟",
                "الكويل يُفك عادةً ببرغي واحد؛ إذا انتقل رقم الأسطوانة في الكود مع الكويل فالعطل في الكويل نفسه.")
                .choice("نعم، والكود انتقل مع الكويل", coil)
                .choice("نعم، والكود بقي في نفس الأسطوانة", plugInjectorMech)
                .choice("لا، ما جرّبت", coilTestNeeded);

        QuestionNode afmTick = ask("M_AFM_TICK",
                "هل تسمع صوت تكتكة (طقطقة) منتظمة من المحرك؟",
                "الصوت يشبه ساعة تتكتك وتزداد سرعته مع زيادة الدوران، ويكون أوضح عند التشغيل أو الهدوء.")
                .choice("نعم، في تكتكة واضحة", afmLifter)
                .choice("لا، ما في صوت", coilSwap);

        QuestionNode cylinder = ask("M_CYL",
                "ما رقم الأسطوانة المذكورة في الكود؟",
                "آخر رقم في الكود هو رقم الأسطوانة، فمثلًا P0306 تعني الأسطوانة رقم 6.")
                .choice("1 أو 4 أو 6 أو 7 (أسطوانات نظام AFM)", afmTick)
                .choice("2 أو 3 أو 5 أو 8", coilSwap);

        QuestionNode random = ask("M_RANDOM",
                "متى تظهر التفتفة بشكل أوضح؟",
                null)
                .choice("عند التشغيل على البارد ثم تخف أو تختفي", coldStart)
                .choice("عند التسارع أو الحمولة أو الطلعات", plugsLoad)
                .choice("طوال الوقت حتى على السرعة الثابتة", randomTick);

        QuestionNode maf = ask("M_MAF",
                "هل نظّفت حساس تدفق الهواء (MAF) أو بدّلت فلتر الهواء مؤخرًا؟",
                null)
                .choice("نعم، والكود ما زال يظهر", fuelSystem)
                .choice("لا، ما سويت", mafDirty);

        QuestionNode lean = ask("M_LEAN",
                "هل تسمع صفيرًا أو هسهسة من المحرك، أو تخف التفتفة عند رفع الدوران؟",
                null)
                .choice("نعم", vacuumLeak)
                .choice("لا", maf);

        QuestionNode plugAge = ask("M_PLUG_AGE",
                "كم قطعت السيارة منذ آخر تبديل للشمعات؟",
                null)
                .choice("أكثر من 100 ألف كم أو لا أعلم", plugsOld)
                .choice("أقل من 100 ألف كم", randomTick);

        QuestionNode noCode = ask("M_NOCODE",
                "ما حالة لمبة فحص المحرك (Check Engine) في الطبلون؟",
                null)
                .choice("تومض (تغمّز)", flashingCel)
                .choice("مضاءة بشكل ثابت", scanNeeded)
                .choice("مطفأة", plugAge);

        QuestionNode root = ask("M_ROOT",
                "هل فحصت السيارة بجهاز الكمبيوتر (OBD2) وطلع لك كود؟",
                "جهاز OBD2 يتوصل بمنفذ تحت الطبلون ويقرأ أكواد الأعطال التي تبدأ بحرف P مثل P0301.")
                .choice("نعم، كود حريق في أسطوانة محددة (من P0301 إلى P0308)", cylinder)
                .choice("نعم، كود حريق عشوائي (P0300)", random)
                .choice("نعم، كود خليط فقير (P0171 أو P0174)", lean)
                .choice("لا، ما فحصت أو ما في كود", noCode);

        return new Symptom("MISFIRE", "تفتفة في المحرك",
                "المحرك يهتز أو يتقطع أو تضعف قوته، وقد تومض لمبة الفحص.", root);
    }
}
