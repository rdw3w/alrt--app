package com.example.data.db

import com.example.data.model.MorningChecklistItem
import com.example.data.model.RoutineTask
import com.example.data.model.ScienceCard
import com.example.data.model.ScienceQuizQuestion

object DefaultData {

    fun getDefaultRoutine(): List<RoutineTask> = listOf(
        RoutineTask(
            id = 1,
            titleHi = "जागना, अलार्म बंद करना व पानी पीना",
            titleEn = "Wake up, dismiss alarm and drink water",
            startTime = "05:00",
            endTime = "05:10",
            category = "HYGIENE",
            orderIndex = 1
        ),
        RoutineTask(
            id = 2,
            titleHi = "शौच, फ्रेश होना और ब्रश करना",
            titleEn = "Toilet, freshening up and brushing",
            startTime = "05:10",
            endTime = "05:30",
            category = "HYGIENE",
            orderIndex = 2
        ),
        RoutineTask(
            id = 3,
            titleHi = "हल्का व्यायाम, स्ट्रेचिंग व वॉक",
            titleEn = "Light exercise and stretching",
            startTime = "05:30",
            endTime = "05:50",
            category = "HEALTH",
            orderIndex = 3
        ),
        RoutineTask(
            id = 4,
            titleHi = "अध्ययन सामग्री व नोट्स तैयार करना",
            titleEn = "Settle down and prepare study material",
            startTime = "05:50",
            endTime = "06:00",
            category = "ROUTINE",
            orderIndex = 4
        ),
        RoutineTask(
            id = 5,
            titleHi = "GK-GS क्लास और नोट्स",
            titleEn = "GK-GS class and notes",
            startTime = "06:00",
            endTime = "08:00",
            category = "STUDY",
            subjectKey = "GK_GS",
            targetMinutes = 120,
            orderIndex = 5
        ),
        RoutineTask(
            id = 6,
            titleHi = "स्नान और तैयार होना",
            titleEn = "Bath and get ready",
            startTime = "08:00",
            endTime = "08:30",
            category = "HYGIENE",
            orderIndex = 6
        ),
        RoutineTask(
            id = 7,
            titleHi = "नाश्ता और अल्प विश्राम",
            titleEn = "Breakfast and short rest",
            startTime = "08:30",
            endTime = "09:00",
            category = "MEALS",
            orderIndex = 7
        ),
        RoutineTask(
            id = 8,
            titleHi = "अंकगणित (Arithmetic Maths) — सत्र 1",
            titleEn = "Arithmetic Maths — Session 1",
            startTime = "09:00",
            endTime = "10:30",
            category = "STUDY",
            subjectKey = "ARITHMETIC_MATHS",
            targetMinutes = 90,
            orderIndex = 8
        ),
        RoutineTask(
            id = 9,
            titleHi = "ब्रेक और पानी पीना",
            titleEn = "Break and water",
            startTime = "10:30",
            endTime = "10:45",
            category = "BREAKS",
            orderIndex = 9
        ),
        RoutineTask(
            id = 10,
            titleHi = "अंकगणित (Arithmetic Maths) — सत्र 2",
            titleEn = "Arithmetic Maths — Session 2",
            startTime = "10:45",
            endTime = "12:15",
            category = "STUDY",
            subjectKey = "ARITHMETIC_MATHS",
            targetMinutes = 90,
            orderIndex = 10
        ),
        RoutineTask(
            id = 11,
            titleHi = "लघु विश्राम (Short break)",
            titleEn = "Short break",
            startTime = "12:15",
            endTime = "12:30",
            category = "BREAKS",
            orderIndex = 11
        ),
        RoutineTask(
            id = 12,
            titleHi = "सामान्य विज्ञान (Science class)",
            titleEn = "Science class",
            startTime = "12:30",
            endTime = "13:30",
            category = "STUDY",
            subjectKey = "SCIENCE",
            targetMinutes = 60,
            orderIndex = 12
        ),
        RoutineTask(
            id = 13,
            titleHi = "दोपहर का भोजन (Lunch)",
            titleEn = "Lunch",
            startTime = "13:30",
            endTime = "14:00",
            category = "MEALS",
            orderIndex = 13
        ),
        RoutineTask(
            id = 14,
            titleHi = "विश्राम व अगले सत्र की तैयारी",
            titleEn = "Rest and prepare for the next session",
            startTime = "14:00",
            endTime = "14:15",
            category = "BREAKS",
            orderIndex = 14
        ),
        RoutineTask(
            id = 15,
            titleHi = "तर्कशक्ति (Reasoning)",
            titleEn = "Reasoning",
            startTime = "14:15",
            endTime = "16:15",
            category = "STUDY",
            subjectKey = "REASONING",
            targetMinutes = 120,
            orderIndex = 15
        ),
        RoutineTask(
            id = 16,
            titleHi = "हल्का नाश्ता व टहलना",
            titleEn = "Snacks and walking break",
            startTime = "16:15",
            endTime = "16:30",
            category = "BREAKS",
            orderIndex = 16
        ),
        RoutineTask(
            id = 17,
            titleHi = "सामान्य हिन्दी (Hindi)",
            titleEn = "Hindi",
            startTime = "16:30",
            endTime = "18:30",
            category = "STUDY",
            subjectKey = "HINDI",
            targetMinutes = 120,
            orderIndex = 17
        ),
        RoutineTask(
            id = 18,
            titleHi = "ब्रेक और जलपान",
            titleEn = "Break and refresh",
            startTime = "18:30",
            endTime = "18:45",
            category = "BREAKS",
            orderIndex = 18
        ),
        RoutineTask(
            id = 19,
            titleHi = "एडवांस मैथ्स (Advance Maths) — सत्र 1",
            titleEn = "Advance Maths — Session 1",
            startTime = "18:45",
            endTime = "20:15",
            category = "STUDY",
            subjectKey = "ADVANCE_MATHS",
            targetMinutes = 90,
            orderIndex = 19
        ),
        RoutineTask(
            id = 20,
            titleHi = "रात्रि का भोजन (Dinner)",
            titleEn = "Dinner",
            startTime = "20:15",
            endTime = "20:45",
            category = "MEALS",
            orderIndex = 20
        ),
        RoutineTask(
            id = 21,
            titleHi = "एडवांस मैथ्स (Advance Maths) — सत्र 2",
            titleEn = "Advance Maths — Session 2",
            startTime = "20:45",
            endTime = "22:15",
            category = "STUDY",
            subjectKey = "ADVANCE_MATHS",
            targetMinutes = 90,
            orderIndex = 21
        ),
        RoutineTask(
            id = 22,
            titleHi = "त्वरित पुनरावलोकन व कल की योजना",
            titleEn = "Quick revision and tomorrow's planning",
            startTime = "22:15",
            endTime = "22:30",
            category = "ROUTINE",
            orderIndex = 22
        ),
        RoutineTask(
            id = 23,
            titleHi = "गहरी निद्रा (Sleep & Rest)",
            titleEn = "Sleep & restorative rest",
            startTime = "22:30",
            endTime = "05:00",
            category = "SLEEP",
            orderIndex = 23
        )
    )

    fun getDefaultMorningChecklist(): List<MorningChecklistItem> = listOf(
        MorningChecklistItem(1, "समय पर जागना (Wake up at 5:00 AM)", "Wake up on time (5:00 AM)", false, 1),
        MorningChecklistItem(2, "गुनगुना पानी पीना (Drink 1-2 glasses of water)", "Drink water", false, 2),
        MorningChecklistItem(3, "शौच व पेट साफ होना (Use the toilet)", "Use the toilet", false, 3),
        MorningChecklistItem(4, "दातून/ब्रश व मुंह धोना (Brush teeth & freshen up)", "Brush teeth and freshen up", false, 4),
        MorningChecklistItem(5, "हल्का व्यायाम व स्ट्रेचिंग (Exercise or stretch)", "Exercise or stretch", false, 5),
        MorningChecklistItem(6, "अध्ययन सामग्री व्यवस्थित करना (Prepare study materials)", "Get study materials ready", false, 6),
        MorningChecklistItem(7, "GK-GS क्लास व नोट्स (Attend GK-GS class)", "Attend GK-GS class", false, 7),
        MorningChecklistItem(8, "ताजे पानी से स्नान करना (Take a bath)", "Take a bath", false, 8),
        MorningChecklistItem(9, "पौष्टिक नाश्ता करना (Eat healthy breakfast)", "Eat breakfast", false, 9)
    )

    val scienceCards: List<ScienceCard> = listOf(
        ScienceCard(
            id = "sci_1",
            category = "PHYSICS",
            titleHi = "न्यूटन के गति के नियम (Newton's Laws)",
            titleEn = "Newton's Laws of Motion",
            formulaOrFact = "F = m × a  |  p = m × v",
            explanationHi = "प्रथम नियम जड़त्व का नियम कहलाता है। द्वितीय नियम बल की परिभाषा (F=ma) देता है। तृतीय नियम क्रिया-प्रतिक्रिया का नियम है।",
            explanationEn = "1st law: Inertia. 2nd law: Force definition (F=ma). 3rd law: Action and Reaction are equal and opposite.",
            examImportance = "Very High"
        ),
        ScienceCard(
            id = "sci_2",
            category = "PHYSICS",
            titleHi = "ओम का नियम व विद्युत धारा (Ohm's Law)",
            titleEn = "Ohm's Law & Electric Current",
            formulaOrFact = "V = I × R  |  P = V × I = I²R",
            explanationHi = "नियत ताप पर चालक में बहने वाली धारा उसके सिरों के विभवान्तर के समानुपाती होती है। प्रतिरोध का SI मात्रक ओम (Ω) है।",
            explanationEn = "Voltage equals current multiplied by resistance. Resistance SI unit is Ohm (Ω).",
            examImportance = "High"
        ),
        ScienceCard(
            id = "sci_3",
            category = "CHEMISTRY",
            titleHi = "pH पैमाना और अम्ल-क्षार (pH Scale)",
            titleEn = "pH Scale, Acids & Bases",
            formulaOrFact = "pH < 7 (Acidic) | pH = 7 (Neutral) | pH > 7 (Basic)",
            explanationHi = "रक्त का pH लगभग 7.4 होता है। शुद्ध जल का 7 होता है। जठर रस (HCl) का pH 1.5 से 2.5 के बीच होता है।",
            explanationEn = "Human blood pH is ~7.4. Pure water is 7. Gastric juice (HCl) is 1.5 to 2.5.",
            examImportance = "Very High"
        ),
        ScienceCard(
            id = "sci_4",
            category = "CHEMISTRY",
            titleHi = "प्रमुख रासायनिक सूत्र व नाम (Chemical Formulas)",
            titleEn = "Essential Chemical Formulas",
            formulaOrFact = "Baking Soda: NaHCO₃ | Washing Soda: Na₂CO₃·10H₂O | Bleaching Powder: CaOCl₂",
            explanationHi = "प्लास्टर ऑफ पेरिस: CaSO₄·½H₂O, जिप्सम: CaSO₄·2H₂O, भारी जल (Heavy Water): D₂O.",
            explanationEn = "Plaster of Paris: CaSO₄·½H₂O, Gypsum: CaSO₄·2H₂O, Heavy Water: D₂O.",
            examImportance = "Very High"
        ),
        ScienceCard(
            id = "sci_5",
            category = "BIOLOGY",
            titleHi = "कोशिका और कोशिकांग (Cell & Organelles)",
            titleEn = "Cell & Cell Organelles",
            formulaOrFact = "माइटोकॉन्ड्रिया = कोशिका का पावरहाउस (ATP)",
            explanationHi = "राइबोसोम प्रोटीन फैक्ट्री है। लाइसोसोम आत्मघाती थैली (Suicidal bag) है। पादप कोशिका भित्ति सेल्युलोज की बनी होती है।",
            explanationEn = "Mitochondria: Powerhouse of the cell (ATP). Ribosome: Protein factory. Lysosome: Suicide bag.",
            examImportance = "Very High"
        ),
        ScienceCard(
            id = "sci_6",
            category = "BIOLOGY",
            titleHi = "विटामिन और उनकी कमी से रोग (Vitamins & Deficiency)",
            titleEn = "Vitamins & Deficiency Diseases",
            formulaOrFact = "Vit A: रतौंधी | Vit B1: बेरी-बेरी | Vit C: स्कर्वी | Vit D: रिकेट्स",
            explanationHi = "विटामिन B व C जल में घुलनशील हैं। विटामिन A, D, E, K वसा में घुलनशील हैं। रक्त का थक्का बनाने में विटामिन K सहायक है।",
            explanationEn = "Water soluble: Vit B & C. Fat soluble: Vit A, D, E, K. Vit K helps in blood clotting.",
            examImportance = "Very High"
        )
    )

    val scienceQuiz: List<ScienceQuizQuestion> = listOf(
        ScienceQuizQuestion(
            id = 1,
            questionHi = "प्रकाश वर्ष (Light Year) निम्नलिखित में से किसकी इकाई है?",
            questionEn = "Light Year is a unit of which of the following?",
            optionsHi = listOf("समय", "दूरी", "प्रकाश की गति", "प्रकाश की तीव्रता"),
            optionsEn = listOf("Time", "Distance", "Speed of Light", "Light Intensity"),
            correctIndex = 1,
            explanationHi = "प्रकाश वर्ष खगोलीय दूरी (Distance) का मात्रक है। 1 प्रकाश वर्ष ≈ 9.46 × 10¹⁵ मीटर।",
            explanationEn = "Light year is a unit of astronomical distance. 1 Light year ≈ 9.46 × 10¹⁵ m."
        ),
        ScienceQuizQuestion(
            id = 2,
            questionHi = "मानव शरीर में रक्त का थक्का जमने में कौन सा विटामिन सहायक होता है?",
            questionEn = "Which vitamin helps in blood clotting in the human body?",
            optionsHi = listOf("विटामिन A", "विटामिन C", "विटामिन D", "विटामिन K"),
            optionsEn = listOf("Vitamin A", "Vitamin C", "Vitamin D", "Vitamin K"),
            correctIndex = 3,
            explanationHi = "विटामिन K प्रोथ्रोम्बिन निर्माण में आवश्यक है, जिससे रक्त का थक्का बनता है।",
            explanationEn = "Vitamin K is essential for synthesis of prothrombin which facilitates blood clotting."
        ),
        ScienceQuizQuestion(
            id = 3,
            questionHi = "शुद्ध जल का pH मान कितना होता है?",
            questionEn = "What is the pH value of pure water?",
            optionsHi = listOf("5", "6", "7", "8"),
            optionsEn = listOf("5", "6", "7", "8"),
            correctIndex = 2,
            explanationHi = "शुद्ध जल उदासीन (Neutral) होता है, जिसका pH मान 7 होता है।",
            explanationEn = "Pure water is neutral with a pH value of 7."
        )
    )
}
