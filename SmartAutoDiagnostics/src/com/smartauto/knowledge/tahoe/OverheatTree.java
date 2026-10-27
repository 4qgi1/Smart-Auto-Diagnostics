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
 * شجرة عَرَض «ارتفاع حرارة المحرك».
 */
final class OverheatTree {

    private OverheatTree() {
    }

    static Symptom build() {

        // ───────────── النتائج ─────────────

        ResultNode coolantLeak = leaf(Diagnosis.builder("COOLANT_LEAK",
                "تسرب في دورة التبريد")
                .explanation("نقص السائل مع بقع أو رائحة حلوة يعني أن السائل يخرج من الدورة: "
                        + "خراطيم، راديتر، مضخة ماء، أو مشعاع التدفئة (Heater Core).")
                .solution("اترك المحرك يبرد تمامًا ثم أضف سائل التبريد المعتمد في دليل المالك كحل إسعافي فقط.")
                .solution("اطلب اختبار ضغط لدورة التبريد لتحديد موضع التسرب.")
                .solution("بدّل الجزء المتسرب (خرطوم أو راديتر أو مضخة ماء) وافحص غطاء الراديتر.")
                .solution("بعد الإصلاح أعد ملء الدورة وأخرج الهواء منها.")
                .severity(Severity.HIGH)
                .confidence(Confidence.MEDIUM)
                .tip("لا تفتح غطاء الراديتر والمحرك ساخن أبدًا؛ البخار قد يسبب حروقًا شديدة."));

        ResultNode headGasket = leaf(Diagnosis.builder("HEAD_GASKET",
                "احتمال تلف وسادة الرأس (Head Gasket) أو تشقق في الرأس")
                .explanation("دخان أبيض كثيف أو زيت بلون الحليب أو فقاعات في خزان التبريد من علامات دخول "
                        + "غازات الاحتراق إلى سائل التبريد أو اختلاطه بالزيت، وهذا يرفع الحرارة بسرعة.")
                .solution("أوقف السيارة ولا تقدها.")
                .solution("اطلب فحص غازات الاحتراق في سائل التبريد (Block Test) وفحص ضغط الأسطوانات.")
                .solution("إن ثبت العطل: تبديل وسادة الرأس مع تسوية الرأس، أو تبديله إذا كان متشققًا.")
                .solution("لا تلجأ لإضافات سد التسرب (Stop Leak)؛ قد تسد الدورة وتزيد المشكلة.")
                .severity(Severity.CRITICAL)
                .confidence(Confidence.MEDIUM));

        ResultNode coolantLossUnknown = leaf(Diagnosis.builder("COOLANT_LOSS_UNKNOWN",
                "فقد سائل تبريد بدون أثر ظاهر")
                .explanation("السائل ينقص دون بقع، وقد يكون تسربًا صغيرًا يتبخر على المحرك الساخن أو "
                        + "تسربًا داخليًا؛ وهذا يحتاج اختبارات لا تكفي فيها العين وحدها.")
                .solution("اطلب اختبار ضغط لدورة التبريد.")
                .solution("اطلب فحص غازات الاحتراق في السائل (Block Test).")
                .solution("افحص الوصلات وأسفل غطاء الراديتر بكشاف ضوء بعد تشغيل قصير.")
                .solution("راقب مستوى السائل يوميًا حتى يتم الإصلاح.")
                .severity(Severity.HIGH)
                .confidence(Confidence.LOW));

        ResultNode fan = leaf(Diagnosis.builder("FAN",
                "مروحة التبريد لا تعمل بكفاءة")
                .explanation("عند الوقوف أو السرعات البطيئة لا يمر هواء كافٍ على الراديتر إلا بالمروحة، "
                        + "فإذا تعطلت أو ضعفت ترتفع الحرارة. قد تكون المروحة كهربائية أو بكلتش حسب سنة الصنع.")
                .solution("افحص فيوز وريليه المروحة (للمراوح الكهربائية).")
                .solution("افحص محرك المروحة أو الكلتش؛ دوران بطيء أو ثقيل عند الحرارة يعني ضعفه.")
                .solution("افحص حساس حرارة السائل الذي يأمر المروحة بالدوران.")
                .solution("نظّف زعانف الراديتر ومكثف المكيف من الغبار والأوساخ.")
                .severity(Severity.HIGH)
                .confidence(Confidence.MEDIUM));

        ResultNode thermostat = leaf(Diagnosis.builder("THERMOSTAT",
                "الثرموستات (منظّم الحرارة) عالق على الإغلاق")
                .explanation("الخرطوم العلوي الحار مع السفلي البارد يعني أن السائل الساخن لا يصل إلى "
                        + "الراديتر لأن الثرموستات لا يفتح، فترتفع الحرارة بسرعة.")
                .solution("بدّل الثرموستات والجوان بقطعة أصلية أو جودة عالية.")
                .solution("بدّل سائل التبريد إذا كان قديمًا أو متسخًا، وأخرج الهواء من الدورة.")
                .solution("إذا لم تتحسن الحرارة فافحص الراديتر ومضخة الماء.")
                .severity(Severity.HIGH)
                .confidence(Confidence.HIGH));

        ResultNode radiatorPump = leaf(Diagnosis.builder("RADIATOR_PUMP",
                "راديتر مسدود أو مضخة ماء ضعيفة")
                .explanation("ارتفاع الحرارة على الطريق السريع أو مع الحمولة مع سخونة الخرطومين يرجّح أن "
                        + "الراديتر لا يطرد الحرارة بكفاءة (انسداد داخلي أو أوساخ خارجية) "
                        + "أو أن مضخة الماء ضعيفة.")
                .solution("اغسل دورة التبريد وافحص الراديتر من انسداد داخلي.")
                .solution("نظّف الزعانف من الخارج.")
                .solution("افحص مضخة الماء: صوت غير طبيعي، تسرب من فتحة التصريف، أو ريشة متآكلة.")
                .solution("بدّل الراديتر أو المضخة حسب نتيجة الفحص.")
                .severity(Severity.HIGH)
                .confidence(Confidence.LOW));

        ResultNode tempSensor = leaf(Diagnosis.builder("TEMP_SENSOR",
                "حساس الحرارة أو قراءة المؤشر خاطئة")
                .explanation("الخراطيم باردة بينما المؤشر مرتفع، وهذا يتعارض مع محرك مرتفع الحرارة فعلًا؛ "
                        + "فيُحتمل أن الحساس أو توصيلته أو العداد يعطي قراءة خاطئة، أو أن في الدورة "
                        + "فقاعات هواء تعيق التدفق.")
                .solution("قارن قراءة الحرارة بجهاز OBD2 (بيانات حية) أو بمقياس حرارة ليزري على جسم الثرموستات.")
                .solution("افحص الحساس وقابسه وبدّله إن ثبت العطل.")
                .solution("تأكد من خلو الدورة من فقاعات الهواء وأخرج الهواء حسب دليل الصيانة.")
                .severity(Severity.MEDIUM)
                .confidence(Confidence.LOW));

        // ───────────── الأسئلة (من الأسفل للأعلى) ─────────────

        QuestionNode speed = ask("H_SPEED",
                "متى تكون الحرارة أعلى؟",
                null)
                .choice("في الزحام أو السرعات البطيئة أو الوقوف", fan)
                .choice("على الطريق السريع أو مع حمولة أو صعود", radiatorPump);

        QuestionNode hoses = ask("H_HOSES",
                "والمحرك في وضع التشغيل الحار، كيف حرارة خرطومي الراديتر (العلوي والسفلي)؟",
                "المس بحذر وبقفاز وبعيدًا عن المروحة والسيور؛ الخراطيم ساخنة جدًا.")
                .choice("العلوي حار والسفلي بارد بوضوح", thermostat)
                .choice("الاثنان حاران", speed)
                .choice("الاثنان باردان تقريبًا والمؤشر مرتفع", tempSensor);

        QuestionNode fanQ = ask("H_FAN",
                "عند وقوف السيارة بعد أن تسخن (أو عند تشغيل المكيف)، هل تدور مروحة التبريد؟",
                "افتح غطاء المحرك بحذر وابقَ بعيدًا عن المروحة وهي تدور.")
                .choice("لا، المروحة ما تدور أو تدور ببطء شديد", fan)
                .choice("نعم، تدور", hoses);

        QuestionNode leakQ = ask("H_LEAK",
                "ماذا تلاحظ مع نقص السائل؟",
                null)
                .choice("بقع سائل تحت السيارة أو رائحة حلوة", coolantLeak)
                .choice("دخان أبيض كثيف من العادم أو زيت بلون الحليب أو فقاعات في الخزان", headGasket)
                .choice("لا أرى شيئًا ظاهرًا", coolantLossUnknown);

        QuestionNode root = ask("H_ROOT",
                "هل مستوى سائل التبريد في الخزان منخفض؟",
                "افحصه والمحرك بارد تمامًا. لا تفتح غطاء الراديتر وهو ساخن أبدًا؛ "
                        + "البخار قد يسبب حروقًا شديدة.")
                .choice("نعم، منخفض أو فارغ", leakQ)
                .choice("لا، طبيعي", fanQ);

        return new Symptom("OVERHEAT", "ارتفاع حرارة المحرك",
                "مؤشر الحرارة مرتفع أو ظهر تحذير سخونة في الطبلون.", root);
    }
}
