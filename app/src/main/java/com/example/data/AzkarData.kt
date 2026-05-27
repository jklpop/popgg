package com.example.data

data class Zikr(
    val id: Int,
    val arabicText: String,
    val translation: String,
    val description: String,
    val targetCount: Int
)

data class AzkarCategory(
    val nameArabic: String,
    val nameEnglish: String,
    val iconName: String,
    val items: List<Zikr>
)

object AzkarData {
    val categories = listOf(
        AzkarCategory(
            nameArabic = "أذكار الصباح",
            nameEnglish = "Morning Azkar",
            iconName = "WbSunny",
            items = listOf(
                Zikr(
                    id = 1,
                    arabicText = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.",
                    translation = "We have entered a new day and with it all dominion belongs to Allah, praise is due to Allah. None has the right to be worshipped but Allah alone, Who has no partner.",
                    description = "يقال مرة واحدة لحفظ اليوم ومباركته.",
                    targetCount = 1
                ),
                Zikr(
                    id = 2,
                    arabicText = "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ وَإِلَيْكَ النُّشُورُ.",
                    translation = "O Allah, by Your leave we have entered the morning, by Your leave we enter the evening, by Your leave we live, by Your leave we die and unto You is our resurrection.",
                    description = "يقال مرة واحدة في أذكار الصباح.",
                    targetCount = 1
                ),
                Zikr(
                    id = 3,
                    arabicText = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ أَصْلِحْ لِي شَأْنِي كُلَّهُ وَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ.",
                    translation = "O Ever Living One, O Sustainer of all, by Your mercy I call upon You to rectify all my affairs. Do not leave me to myself even for the blink of an eye.",
                    description = "يقال 3 مرات لنيل الكفاية والرحمة الإلهية.",
                    targetCount = 3
                ),
                Zikr(
                    id = 4,
                    arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ.",
                    translation = "Glory be to Allah and His is the praise.",
                    description = "يقال 100 مرة لمغفرة الذنوب والخطايا.",
                    targetCount = 100
                )
            )
        ),
        AzkarCategory(
            nameArabic = "أذكار المساء",
            nameEnglish = "Evening Azkar",
            iconName = "NightsStay",
            items = listOf(
                Zikr(
                    id = 5,
                    arabicText = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.",
                    translation = "We have entered the evening and with it all dominion belongs to Allah, praise is due to Allah. None has the right to be worshipped but Allah alone.",
                    description = "يقال مرة واحدة عند المغيب لطمأنينة النفس والكفاية.",
                    targetCount = 1
                ),
                Zikr(
                    id = 6,
                    arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْجُبْنِ وَالْبُخْلِ، وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ.",
                    translation = "O Allah, I seek refuge in You from anxiety and sorrow, weakness and laziness, miserliness and cowardice, the burden of debts and from being overpowered by men.",
                    description = "يقال مرة واحدة لإزالة الهم والكرب.",
                    targetCount = 1
                ),
                Zikr(
                    id = 7,
                    arabicText = "اللَّهُمَّ عافِني في بَدَني، اللَّهُمَّ عافِني في سَمْعي، اللَّهُمَّ عافِني في بَصَري، لا إلهَ إلاَّ أَنْتَ.",
                    translation = "O Allah, grant health to my body; O Allah, grant health to my hearing; O Allah, grant health to my sight. There is no deity but You.",
                    description = "يقال 3 مرات لتمام العافية في البدن والحواس.",
                    targetCount = 3
                )
            )
        ),
        AzkarCategory(
            nameArabic = "أذكار بعد الصلاة",
            nameEnglish = "Post-Prayer Azkar",
            iconName = "CheckCircle",
            items = listOf(
                Zikr(
                    id = 8,
                    arabicText = "أَسْتَغْفِرُ اللهَ.",
                    translation = "I seek Allah's forgiveness.",
                    description = "تقال 3 مرات مباشرة فور تسليم الفريضة.",
                    targetCount = 3
                ),
                Zikr(
                    id = 9,
                    arabicText = "اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ ذَا الْجَلَالِ وَالْإِكْرَامِ.",
                    translation = "O Allah, You are Peace and from You comes peace. Blessed are You, Owner of majesty and honor.",
                    description = "تقال مرة واحدة بعد الاستغفار.",
                    targetCount = 1
                ),
                Zikr(
                    id = 10,
                    arabicText = "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ.",
                    translation = "O Allah, help me to remember You, be grateful to You, and worship You in an excellent manner.",
                    description = "توصية نبوية تقال دبر كل صلاة مكتوبة.",
                    targetCount = 1
                )
            )
        ),
        AzkarCategory(
            nameArabic = "أذكار النوم",
            nameEnglish = "Sleep Supplications",
            iconName = "Bedtime",
            items = listOf(
                Zikr(
                    id = 11,
                    arabicText = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي وَبِكَ أَرْفَعُهُ، إِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ.",
                    translation = "In Your name, my Lord, I lay my side down, and by You I raise it up. If You should take my soul, have mercy on it, and if You should return it, preserve it.",
                    description = "يقال مرة واحدة على الشق الأيمن عند النوم.",
                    targetCount = 1
                ),
                Zikr(
                    id = 12,
                    arabicText = "اللَّهُمَّ قِنِي عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ.",
                    translation = "O Allah, save me from Your punishment on the Day You resurrect Your slaves.",
                    description = "يقال 3 مرات قبل النوم.",
                    targetCount = 3
                )
            )
        )
    )
}
