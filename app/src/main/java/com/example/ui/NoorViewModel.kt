package com.example.ui

import android.app.Application
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.AthanBroadcastReceiver
import com.example.data.*
import com.example.network.GeminiContent
import com.example.network.GeminiManager
import com.example.network.GeminiPart
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MidnightBlue
import com.example.ui.theme.SoftGold
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val isUser: Boolean,
    val text: String,
    val timestamp: String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
)

data class PrayerTiming(
    val nameArabic: String,
    val nameEnglish: String,
    val timeString: String, // HH:mm
    val isPassed: Boolean = false,
    val testTag: String
)

data class SelectedCity(
    val nameArabic: String,
    val nameEnglish: String,
    val lat: Double,
    val lon: Double,
    // Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha times
    val timings: List<String>
)

class NoorViewModel(application: Application) : AndroidViewModel(application) {

    private val database = NoorDatabase.getDatabase(application)
    private val repository = NoorRepository(database.noorDao())
    val sharedPrefsByContext = application.getSharedPreferences("noor_smart_tasbih_prefs", android.content.Context.MODE_PRIVATE)

    // --- NAVIGATION & ONBOARDING STATE ---
    var currentScreen by mutableStateOf("splash") // splash, onboarding, main
    var onboardingStep by mutableStateOf(1)
    var activeTab by mutableStateOf("home") // home, quran, azkar, qibla, progress, settings

    // --- USER PROFILE & GAMIFICATION ---
    val userProgress: StateFlow<UserProgress?> = repository.userProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProgress(points = 0))

    val allPrayerTracks: StateFlow<List<PrayerTrack>> = repository.allPrayerTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBookmarks: StateFlow<List<QuranBookmark>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotes: StateFlow<List<QuranNote>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live daily track for selected date
    var todayDateString by mutableStateOf("")
    var todayPrayerTrack by mutableStateOf(PrayerTrack(dateString = ""))

    // --- PREFERENCES ---
    var selectedLanguage by mutableStateOf("ar") // ar, en, fr, tr, ur
    var darkThemeEnabled by mutableStateOf(true)
    var accentAccentColor by mutableStateOf(EmeraldGreen) // Emerald, Blue, Gold
    var selectedAzanSound by mutableStateOf("Makkah Adhan") // Medina, Al-Aqsa, Cairo
    var azanNotificationEnabled by mutableStateOf(true)

    // --- LATEST FEEDS CONTENT ---
    var ayahOfTheDayArabic = "إِنَّ مَعَ الْعُسْرِ يُسْرًا"
    var ayahOfTheDayTranslation = "Indeed, with hardship [will be] ease."
    var ayahOfTheDayRef = "Surah Ash-Sharh [94:6]"

    var hadithOfTheDayArabic = "«الدُّعَاءُ هُوَ الْعِبَادَةُ»"
    var hadithOfTheDayTranslation = "\"Supplication (Dua) is indeed the core of worship.\""
    var hadithOfTheDayRef = "Sunan Tirmidhi"

    var duaOfTheDayArabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا وَرِزْقًا طَيِّبًا وَعَمَلًا مُتَقَبَّلًا"
    var duaOfTheDayTranslation = "O Allah, I ask You for beneficial knowledge, wholesome sustenance, and accepted deeds."

    // --- PRAYER TIMES & CITIES ---
    val cities = listOf(
        SelectedCity("مكة المكرمة", "Makkah", 21.3891, 39.8579, listOf("04:35", "05:54", "12:22", "15:41", "18:50", "20:20")),
        SelectedCity("القاهرة", "Cairo", 30.0444, 31.2357, listOf("03:40", "05:10", "12:00", "15:35", "18:50", "20:20")),
        SelectedCity("لندن", "London", 51.5074, -0.1278, listOf("02:55", "04:48", "13:02", "17:15", "21:12", "23:05")),
        SelectedCity("إسطنبول", "Istanbul", 41.0082, 28.9784, listOf("03:42", "05:28", "13:08", "17:02", "20:41", "22:15")),
        SelectedCity("جاكرتا", "Jakarta", -6.2088, 106.8456, listOf("04:42", "05:58", "11:54", "15:16", "17:48", "19:02"))
    )
    var selectedCityIndex by mutableStateOf(0)
    var prayerTimingsList by mutableStateOf<List<PrayerTiming>>(emptyList())
    var nextPrayerName by mutableStateOf("Fajr")
    var nextPrayerCountdown by mutableStateOf("00:00:00")

    // --- QURAN STATE ---
    var quranSearchQuery by mutableStateOf("")
    var selectedSurah by mutableStateOf<SurahMetadata?>(null)
    var currentSurahVerses by mutableStateOf<List<Ayah>>(emptyList())
    var activeVerseHighlight by mutableStateOf(0) // 0 means no active highlight
    var quranTranslationVisible by mutableStateOf(true)
    var quranTafsirVisible by mutableStateOf(false)
    var activeNoteInputState by mutableStateOf("")

    // Real-world Quran Online / Offline and Audio variables
    var isQuranLoading by mutableStateOf(false)
    var quranErrorMsg by mutableStateOf<String?>(null)
    val allDownloadedSurahs = repository.allDownloadedSurahs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val downloadingSurahIds = mutableStateListOf<Int>()
    var highlightedAyahsSet by mutableStateOf<Set<String>>(loadHighlightsFromPrefs())
    
    // Last Read/Continue Reading states
    var lastReadSurahId by mutableStateOf(sharedPrefsByContext.getInt("last_read_surah_id", 0))
    var lastReadSurahName by mutableStateOf(sharedPrefsByContext.getString("last_read_surah_name", "") ?: "")
    var lastReadAyahNumber by mutableStateOf(sharedPrefsByContext.getInt("last_read_ayah_number", 0))
    var lastReadTimestamp by mutableStateOf(sharedPrefsByContext.getLong("last_read_timestamp", 0L))

    // Real Audio Playback variables
    var isAudioPlaying by mutableStateOf(false)
    var selectedReciter by mutableStateOf("Mishary Al-Afasy") // Mishary, Abdul Basit, Al-Ghamdi
    var activePlayingAyahNumber by mutableStateOf<Int?>(null)
    private var quranMediaPlayer: MediaPlayer? = null
    private var playbackJob: Job? = null

    // --- AZKAR STATE ---
    var activeCategoryIndex by mutableStateOf(0)
    var azkarCountsState = mutableMapOf<Int, Int>() // maps zikr.id -> remaining count

    // --- SMART TASBIH STATE & PERSISTENCE ---

    var tasbihDhikrs = listOf("سبحان الله", "الحمد لله", "الله أكبر", "لا إله إلا الله", "أستغفر الله")
    var selectedDhikrIndex by mutableStateOf(sharedPrefsByContext.getInt("selected_dhikr_idx", 0))
    var tasbihSessionCount by mutableStateOf(sharedPrefsByContext.getInt("tasbih_session_cnt", 0))
    var tasbihTotalCount by mutableStateOf(sharedPrefsByContext.getInt("tasbih_total_cnt", 0))
    var tasbihDailyCount by mutableStateOf(sharedPrefsByContext.getInt("tasbih_daily_cnt", 0))
    var tasbihDailyGoal by mutableStateOf(sharedPrefsByContext.getInt("tasbih_daily_goal", 100))
    var tasbihRoundTarget by mutableStateOf(sharedPrefsByContext.getInt("tasbih_round_target", 33))
    var hapticVibrateEnabled by mutableStateOf(sharedPrefsByContext.getBoolean("tasbih_vibrate", true))

    // For Undo: we can keep a history stack
    private val tasbihUndoStack = mutableListOf<Pair<Int, Int>>() // list of (sessionCount, totalCount)

    // Unlocked achievements state (loaded from prefs as CSV)
    var unlockedAchievements by mutableStateOf(loadAchievementsFromPrefs())

    // Weekly history (7 days)
    var tasbihWeeklyHistory by mutableStateOf(loadWeeklyHistoryFromPrefs())

    // --- SMART ATHAN STATE & PERSISTENCE ---
    var azanSettingsSilentMode by mutableStateOf(sharedPrefsByContext.getBoolean("azan_silent_mode", false))
    var azanPreNotificationEnabled by mutableStateOf(sharedPrefsByContext.getBoolean("azan_pre_notif", true))
    var azanPreNotificationMinutes by mutableStateOf(sharedPrefsByContext.getInt("azan_pre_notif_mins", 15))

    // Sound and status mapping per prayer
    var azanSoundForFajr by mutableStateOf(sharedPrefsByContext.getString("azan_sound_Fajr", "أذان مكة المكرّمة (الحرم)") ?: "أذان مكة المكرّمة (الحرم)")
    var azanSoundForDhuhr by mutableStateOf(sharedPrefsByContext.getString("azan_sound_Dhuhr", "أذان المدينة المنوّرة (النبوي)") ?: "أذان المدينة المنوّرة (النبوي)")
    var azanSoundForAsr by mutableStateOf(sharedPrefsByContext.getString("azan_sound_Asr", "أذان المسجد الأقصى المبارك") ?: "أذان المسجد الأقصى المبارك")
    var azanSoundForMaghrib by mutableStateOf(sharedPrefsByContext.getString("azan_sound_Maghrib", "أذان مكة المكرّمة (الحرم)") ?: "أذان مكة المكرّمة (الحرم)")
    var azanSoundForIsha by mutableStateOf(sharedPrefsByContext.getString("azan_sound_Isha", "أذان القاهرة الهادئ") ?: "أذان القاهرة الهادئ")

    var azanEnabledForFajr by mutableStateOf(sharedPrefsByContext.getBoolean("azan_enabled_Fajr", true))
    var azanEnabledForDhuhr by mutableStateOf(sharedPrefsByContext.getBoolean("azan_enabled_Dhuhr", true))
    var azanEnabledForAsr by mutableStateOf(sharedPrefsByContext.getBoolean("azan_enabled_Asr", true))
    var azanEnabledForMaghrib by mutableStateOf(sharedPrefsByContext.getBoolean("azan_enabled_Maghrib", true))
    var azanEnabledForIsha by mutableStateOf(sharedPrefsByContext.getBoolean("azan_enabled_Isha", true))

    // In-app test player for Wave Animation and previews
    private var testMediaPlayer: MediaPlayer? = null
    var isTestAthanPlaying by mutableStateOf(false)
    var testAthanPrayerSelected by mutableStateOf("Fajr")
    var audioWaveAnimationProgress = mutableStateListOf<Float>()
    private var waveJob: Job? = null

    // --- SMART PREFERENCES & HELPERS ---
    private fun loadAchievementsFromPrefs(): List<String> {
        val s = sharedPrefsByContext.getString("unlocked_achievements", "") ?: ""
        return if (s.isEmpty()) emptyList() else s.split(",")
    }

    private fun saveAchievementsToPrefs(list: List<String>) {
        sharedPrefsByContext.edit().putString("unlocked_achievements", list.joinToString(",")).apply()
        unlockedAchievements = list
    }

    private fun loadWeeklyHistoryFromPrefs(): List<Int> {
        val s = sharedPrefsByContext.getString("weekly_history", "45,85,120,60,95,150,0") ?: "45,85,120,60,95,150,0"
        return s.split(",").map { it.toIntOrNull() ?: 0 }
    }

    fun saveWeeklyHistoryToPrefs() {
        val list = tasbihWeeklyHistory.toMutableList()
        if (list.size >= 7) {
            list[6] = tasbihDailyCount
        }
        sharedPrefsByContext.edit().putString("weekly_history", list.joinToString(",")).apply()
        tasbihWeeklyHistory = list
    }

    fun saveTasbihStateToPrefs() {
        sharedPrefsByContext.edit().apply {
            putInt("selected_dhikr_idx", selectedDhikrIndex)
            putInt("tasbih_session_cnt", tasbihSessionCount)
            putInt("tasbih_total_cnt", tasbihTotalCount)
            putInt("tasbih_daily_cnt", tasbihDailyCount)
            putInt("tasbih_daily_goal", tasbihDailyGoal)
            putInt("tasbih_round_target", tasbihRoundTarget)
            putBoolean("tasbih_vibrate", hapticVibrateEnabled)
        }.apply()
    }

    fun selectDhikr(index: Int) {
        if (index in tasbihDhikrs.indices) {
            selectedDhikrIndex = index
            tasbihSessionCount = 0
            saveTasbihStateToPrefs()
        }
    }

    fun updateDailyGoal(goal: Int) {
        if (goal > 0) {
            tasbihDailyGoal = goal
            saveTasbihStateToPrefs()
        }
    }

    fun incrementTasbih() {
        if (tasbihUndoStack.size >= 10) {
            tasbihUndoStack.removeAt(0)
        }
        tasbihUndoStack.add(Pair(tasbihSessionCount, tasbihTotalCount))

        tasbihSessionCount++
        tasbihTotalCount++
        tasbihDailyCount++
        
        saveTasbihStateToPrefs()
        saveWeeklyHistoryToPrefs()
        checkAchievementsUnlocked()

        if (tasbihSessionCount >= tasbihRoundTarget && tasbihRoundTarget > 0) {
            tasbihSessionCount = 0
            saveTasbihStateToPrefs()
            viewModelScope.launch(Dispatchers.IO) {
                addUserPoints(12) // +12 pts reward
            }
        }
    }

    fun undoLastTasbih() {
        if (tasbihUndoStack.isNotEmpty()) {
            val last = tasbihUndoStack.removeAt(tasbihUndoStack.size - 1)
            val prevSession = last.first
            val prevTotal = last.second
            val deltaTotal = tasbihTotalCount - prevTotal
            
            tasbihSessionCount = prevSession
            tasbihTotalCount = prevTotal
            tasbihDailyCount = (tasbihDailyCount - deltaTotal).coerceAtLeast(0)
            
            saveTasbihStateToPrefs()
            saveWeeklyHistoryToPrefs()
        }
    }

    fun resetTasbihCount() {
        tasbihUndoStack.add(Pair(tasbihSessionCount, tasbihTotalCount))
        tasbihSessionCount = 0
        tasbihTotalCount = 0
        tasbihDailyCount = 0
        saveTasbihStateToPrefs()
        saveWeeklyHistoryToPrefs()
    }

    private fun checkAchievementsUnlocked() {
        val currentUnlocked = unlockedAchievements.toMutableList()
        var newlyUnlocked = false
        
        if (tasbihTotalCount >= 100 && !currentUnlocked.contains("starter")) {
            currentUnlocked.add("starter")
            newlyUnlocked = true
            viewModelScope.launch { addUserPoints(50) }
        }
        
        if (tasbihDailyCount >= tasbihDailyGoal && !currentUnlocked.contains("constant")) {
            currentUnlocked.add("constant")
            newlyUnlocked = true
            viewModelScope.launch { addUserPoints(100) }
        }
        
        if (tasbihTotalCount >= 1000 && !currentUnlocked.contains("golden")) {
            currentUnlocked.add("golden")
            newlyUnlocked = true
            viewModelScope.launch { addUserPoints(200) }
        }

        if (newlyUnlocked) {
            saveAchievementsToPrefs(currentUnlocked)
        }
    }

    fun togglePrayerAthanStatus(prayerName: String, enabled: Boolean) {
        sharedPrefsByContext.edit().putBoolean("azan_enabled_$prayerName", enabled).apply()
        when (prayerName) {
            "Fajr" -> azanEnabledForFajr = enabled
            "Dhuhr" -> azanEnabledForDhuhr = enabled
            "Asr" -> azanEnabledForAsr = enabled
            "Maghrib" -> azanEnabledForMaghrib = enabled
            "Isha" -> azanEnabledForIsha = enabled
        }
        scheduleNextUpcomingAthanAlarm()
    }

    fun saveMuezzinForPrayer(prayerName: String, voice: String) {
        sharedPrefsByContext.edit().putString("azan_sound_$prayerName", voice).apply()
        when (prayerName) {
            "Fajr" -> azanSoundForFajr = voice
            "Dhuhr" -> azanSoundForDhuhr = voice
            "Asr" -> azanSoundForAsr = voice
            "Maghrib" -> azanSoundForMaghrib = voice
            "Isha" -> azanSoundForIsha = voice
        }
        scheduleNextUpcomingAthanAlarm()
    }

    fun toggleSilentMode(silent: Boolean) {
        azanSettingsSilentMode = silent
        sharedPrefsByContext.edit().putBoolean("azan_silent_mode", silent).apply()
    }

    fun togglePrePrayerStatus(enabled: Boolean) {
        azanPreNotificationEnabled = enabled
        sharedPrefsByContext.edit().putBoolean("azan_pre_notif", enabled).apply()
    }

    fun updatePrePrayerMinutes(mins: Int) {
        azanPreNotificationMinutes = mins
        sharedPrefsByContext.edit().putInt("azan_pre_notif_mins", mins).apply()
    }

    fun togglePreviewAthan(prayerKey: String) {
        if (isTestAthanPlaying && testAthanPrayerSelected == prayerKey) {
            stopPreviewAthan()
        } else {
            stopPreviewAthan()
            testAthanPrayerSelected = prayerKey
            isTestAthanPlaying = true
            
            val muezzin = when (prayerKey) {
                "Fajr" -> azanSoundForFajr
                "Dhuhr" -> azanSoundForDhuhr
                "Asr" -> azanSoundForAsr
                "Maghrib" -> azanSoundForMaghrib
                "Isha" -> azanSoundForIsha
                else -> "أذان مكة"
            }
            
            val url = when {
                muezzin.contains("المدينة") -> "https://download.quranicaudio.com/adhan/madinah.mp3"
                muezzin.contains("الأقصى") -> "https://download.quranicaudio.com/adhan/alaqsa.mp3"
                muezzin.contains("القاهرة") -> "https://download.quranicaudio.com/adhan/abdul_basit.mp3"
                else -> "https://download.quranicaudio.com/adhan/makkah.mp3"
            }
            
            audioWaveAnimationProgress.clear()
            for (i in 0..15) {
                audioWaveAnimationProgress.add(0.2f + (i % 3) * 0.2f)
            }
            
            waveJob?.cancel()
            waveJob = viewModelScope.launch(Dispatchers.Main) {
                val rand = Random()
                while (isTestAthanPlaying) {
                    for (i in audioWaveAnimationProgress.indices) {
                        try {
                            audioWaveAnimationProgress[i] = 0.2f + rand.nextFloat() * 0.8f
                        } catch (_: Exception) {}
                    }
                    delay(120)
                }
            }
            
            testMediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                try {
                    setDataSource(url)
                    prepareAsync()
                    setOnPreparedListener { player ->
                        player.start()
                    }
                    setOnCompletionListener {
                        stopPreviewAthan()
                    }
                    setOnErrorListener { _, _, _ ->
                        stopPreviewAthan()
                        false
                    }
                } catch (e: Exception) {
                    stopPreviewAthan()
                }
            }
        }
    }
    
    fun stopPreviewAthan() {
        isTestAthanPlaying = false
        waveJob?.cancel()
        try {
            testMediaPlayer?.stop()
            testMediaPlayer?.release()
        } catch (_: Exception) {}
        testMediaPlayer = null
    }

    fun scheduleNextUpcomingAthanAlarm() {
        val alarmManager = getApplication<Application>().getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMin = now.get(Calendar.MINUTE)
        val currentTotalSec = (currentHour * 3600) + (currentMin * 60)
        
        val names = listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha")
        val parsedTimes = prayerTimingsList.map { timing ->
            val parts = timing.timeString.split(":")
            val h = parts.getOrNull(0)?.toIntOrNull() ?: 12
            val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
            (h * 3600) + (m * 60)
        }
        
        if (parsedTimes.isEmpty()) return
        
        var targetIndex = -1
        var isTomorrow = false
        
        for (i in parsedTimes.indices) {
            if (names[i] == "Sunrise") continue
            if (currentTotalSec < parsedTimes[i]) {
                targetIndex = i
                break
            }
        }
        
        if (targetIndex == -1) {
            targetIndex = 0
            isTomorrow = true
        }
        
        val targetPrayerName = names[targetIndex]
        val targetTimeString = prayerTimingsList.getOrNull(targetIndex)?.timeString ?: "12:00"
        val parts = targetTimeString.split(":")
        val ruleHour = parts.getOrNull(0)?.toIntOrNull() ?: 12
        val ruleMin = parts.getOrNull(1)?.toIntOrNull() ?: 0
        
        val triggerCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, ruleHour)
            set(Calendar.MINUTE, ruleMin)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (isTomorrow) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        val muezzinVoice = when (targetPrayerName) {
            "Fajr" -> azanSoundForFajr
            "Dhuhr" -> azanSoundForDhuhr
            "Asr" -> azanSoundForAsr
            "Maghrib" -> azanSoundForMaghrib
            "Isha" -> azanSoundForIsha
            else -> "أذان مكة المكرّمة (الحرم)"
        }
        
        val intent = Intent(getApplication(), AthanBroadcastReceiver::class.java).apply {
            putExtra("PRAYER_NAME", targetPrayerName)
            putExtra("MUEZZIN", muezzinVoice)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            1001,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerCal.timeInMillis, pendingIntent)
                    } else {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerCal.timeInMillis, pendingIntent)
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerCal.timeInMillis, pendingIntent)
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerCal.timeInMillis, pendingIntent)
            }
        } catch (_: Exception) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerCal.timeInMillis, pendingIntent)
        }
    }

    // --- COMPASS & QIBLA STATE ---
    var compassDegree by mutableStateOf(0f)
    var computedQiblaAngle by mutableStateOf(252f) // Mecca angle from north
    var isMeccaAligned by mutableStateOf(false)
    var arModeActive by mutableStateOf(false)
    private var compassJob: Job? = null

    // --- AI ASSISTANT STATE ---
    var aiChatHistory = mutableListOf<ChatMessage>()
    var aiApiResponseLoading by mutableStateOf(false)
    var assistantTypingText by mutableStateOf("")

    // Time update loop
    private var timerJob: Job? = null

    init {
        // Initial setup for dates
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        todayDateString = sdf.format(Date())

        loadPrayerTimesForCity()
        loadOrCreateTodayPrayerTrack()
        startGlobalClockAndCompass()

        // Setup AI Assistant welcome message
        aiChatHistory.add(
            ChatMessage(
                isUser = false,
                text = "السلام عليكم ورحمة الله وبركاته!\nأنا مساعدك الإسلامي الذكي 'نور'. كيف يمكنني إعانتك اليوم أو تفسير آية كريمة لك؟\n\nPeace be upon you! I am Noor, your AI Islamic companion. How can I assist you with Quran queries or prayers today?"
            )
        )
    }

    // --- TIMING COUNTER LOOP ---
    private fun startGlobalClockAndCompass() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                updatePrayerCountdown()
                delay(1000)
            }
        }

        // Simulate physical gyroscope movement on emulator so compass shifts elegantly
        compassJob?.cancel()
        compassJob = viewModelScope.launch(Dispatchers.Main) {
            var step = 1.2f
            while (true) {
                // Sway the compass angle slowly back and forth to look organic and live
                val nextDeg = (compassDegree + step)
                compassDegree = if (nextDeg >= 360f) 0f else if (nextDeg < 0) 359f else nextDeg
                if (compassDegree > 255f) step = -0.5f
                if (compassDegree < 248f) step = 0.5f

                // Mecca is at 252 degrees
                val diff = kotlin.math.abs(compassDegree - computedQiblaAngle)
                isMeccaAligned = diff < 3.0f

                delay(150)
            }
        }
    }

    private fun loadPrayerTimesForCity() {
        val city = cities[selectedCityIndex]
        val times = city.timings // listOf("04:35", "05:54", "12:22", "15:41", "18:50", "20:20")
        prayerTimingsList = listOf(
            PrayerTiming("الفجر", "Fajr", times[0], testTag = "fajr_time_card"),
            PrayerTiming("الشروق", "Sunrise", times[1], testTag = "sunrise_time_card"),
            PrayerTiming("الظهر", "Dhuhr", times[2], testTag = "dhuhr_time_card"),
            PrayerTiming("العصر", "Asr", times[3], testTag = "asr_time_card"),
            PrayerTiming("المغرب", "Maghrib", times[4], testTag = "maghrib_time_card"),
            PrayerTiming("العشاء", "Isha", times[5], testTag = "isha_time_card")
        )
    }

    private fun loadOrCreateTodayPrayerTrack() {
        viewModelScope.launch {
            val dbTrack = withContext(Dispatchers.IO) {
                repository.getPrayerTrackForDate(todayDateString)
            }
            if (dbTrack != null) {
                todayPrayerTrack = dbTrack
            } else {
                val newTrack = PrayerTrack(dateString = todayDateString)
                withContext(Dispatchers.IO) {
                    repository.insertPrayerTrack(newTrack)
                }
                todayPrayerTrack = newTrack
            }
        }
    }

    private fun updatePrayerCountdown() {
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMin = now.get(Calendar.MINUTE)
        val currentSec = now.get(Calendar.SECOND)
        val currentTotalSec = (currentHour * 3600) + (currentMin * 60) + currentSec

        // Convert times to total seconds
        var nextTimeSec = -1
        var selectedName = "Fajr"

        val names = listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha")
        val parsedTimes = prayerTimingsList.map { timing ->
            val parts = timing.timeString.split(":")
            val h = parts[0].toInt()
            val m = parts[1].toInt()
            (h * 3600) + (m * 60)
        }

        if (parsedTimes.isEmpty()) return

        for (i in parsedTimes.indices) {
            if (currentTotalSec < parsedTimes[i]) {
                nextTimeSec = parsedTimes[i]
                selectedName = names[i]
                break
            }
        }

        // It is after Isha, next prayer is Fajr tomorrow
        if (nextTimeSec == -1) {
            selectedName = "Fajr"
            nextTimeSec = parsedTimes[0] + (24 * 3600) // add 24 hours
        }

        nextPrayerName = selectedName

        // Calculate delta
        val diffSec = if (nextTimeSec >= currentTotalSec) {
            nextTimeSec - currentTotalSec
        } else {
            (nextTimeSec + (24 * 3600)) - currentTotalSec
        }

        val h = diffSec / 3600
        val m = (diffSec % 3600) / 60
        val s = diffSec % 60
        nextPrayerCountdown = String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
    }

    fun selectCity(index: Int) {
        selectedCityIndex = index
        loadPrayerTimesForCity()
        updatePrayerCountdown()
    }

    // --- PRAYER TRACKING & REWARD ENGINE ---
    fun togglePrayerTracked(prayerKey: String) {
        viewModelScope.launch {
            val track = todayPrayerTrack
            var updatedTrack = track

            var pointsToAdd = 0
            when (prayerKey.lowercase()) {
                "fajr" -> {
                    val newVal = !track.fajr
                    updatedTrack = track.copy(fajr = newVal)
                    pointsToAdd = if (newVal) 15 else -15 // Fajr grants more blessings!
                }
                "dhuhr" -> {
                    val newVal = !track.dhuhr
                    updatedTrack = track.copy(dhuhr = newVal)
                    pointsToAdd = if (newVal) 10 else -10
                }
                "asr" -> {
                    val newVal = !track.asr
                    updatedTrack = track.copy(asr = newVal)
                    pointsToAdd = if (newVal) 10 else -10
                }
                "maghrib" -> {
                    val newVal = !track.maghrib
                    updatedTrack = track.copy(maghrib = newVal)
                    pointsToAdd = if (newVal) 10 else -10
                }
                "isha" -> {
                    val newVal = !track.isha
                    updatedTrack = track.copy(isha = newVal)
                    pointsToAdd = if (newVal) 10 else -10
                }
            }

            withContext(Dispatchers.IO) {
                repository.insertPrayerTrack(updatedTrack)
            }
            todayPrayerTrack = updatedTrack

            // Add points to UserProgress
            addUserPoints(pointsToAdd)
        }
    }

    private suspend fun addUserPoints(delta: Int) {
        val currentProg = userProgress.value ?: UserProgress()
        val newPoints = (currentProg.points + delta).coerceAtLeast(0)
        // Level up dynamic scale: every 100 points
        val newLevel = (newPoints / 100) + 1
        val newStreak = if (delta > 0 && currentProg.streakDays == 0) 1 else currentProg.streakDays

        val updatedProgress = currentProg.copy(
            points = newPoints,
            level = newLevel.coerceAtLeast(1),
            streakDays = newStreak
        )
        repository.insertUserProgress(updatedProgress)
    }

    // --- QURAN INTERACTIONS ---
    private fun loadHighlightsFromPrefs(): Set<String> {
        val s = sharedPrefsByContext.getStringSet("quran_highlights_set", emptySet()) ?: emptySet()
        return s
    }
    
    fun toggleAyahHighlight(surahId: Int, ayahNumber: Int) {
        val key = "${surahId}_$ayahNumber"
        val currentSet = highlightedAyahsSet.toMutableSet()
        if (currentSet.contains(key)) {
            currentSet.remove(key)
        } else {
            currentSet.add(key)
        }
        highlightedAyahsSet = currentSet
        sharedPrefsByContext.edit().putStringSet("quran_highlights_set", currentSet).apply()
    }
    
    fun isAyahHighlighted(surahId: Int, ayahNumber: Int): Boolean {
        return highlightedAyahsSet.contains("${surahId}_$ayahNumber")
    }

    fun saveLastReadPosition(surahId: Int, surahName: String, ayahNumber: Int) {
        lastReadSurahId = surahId
        lastReadSurahName = surahName
        lastReadAyahNumber = ayahNumber
        lastReadTimestamp = System.currentTimeMillis()
        
        sharedPrefsByContext.edit().apply {
            putInt("last_read_surah_id", surahId)
            putString("last_read_surah_name", surahName)
            putInt("last_read_ayah_number", ayahNumber)
            putLong("last_read_timestamp", lastReadTimestamp)
        }.apply()
    }

    fun downloadSurah(surah: SurahMetadata) {
        if (downloadingSurahIds.contains(surah.id)) return
        downloadingSurahIds.add(surah.id)
        viewModelScope.launch {
            try {
                // Fetch verses from AlQuran API under Dispatchers.IO (managed by QuranApiClient)
                val verses = com.example.network.QuranApiClient.fetchSurahVerses(surah.id)
                
                // Serialize with JSONArray
                val jsonArr = JSONArray()
                for (ay in verses) {
                    val obj = JSONObject()
                    obj.put("num", ay.number)
                    obj.put("textAr", ay.textArabic)
                    obj.put("textEn", ay.textEnglish)
                    obj.put("tafsir", ay.tafsir)
                    obj.put("globalNum", ay.globalNumber)
                    obj.put("pageNum", ay.pageNumber)
                    jsonArr.put(obj)
                }
                
                val downloaded = DownloadedSurah(
                    surahId = surah.id,
                    nameArabic = surah.nameArabic,
                    nameEnglish = surah.nameEnglish,
                    revelationType = surah.type,
                    versesCount = surah.versesCount,
                    versesJson = jsonArr.toString()
                )
                
                withContext(Dispatchers.IO) {
                    repository.insertDownloadedSurah(downloaded)
                }
                
                // Grant points for downloading offline first!
                addUserPoints(50)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                downloadingSurahIds.remove(surah.id)
            }
        }
    }

    fun deleteDownloadedSurah(surahId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDownloadedSurah(surahId)
        }
    }

    fun isSurahDownloadedLocally(surahId: Int): Boolean {
        return allDownloadedSurahs.value.any { it.surahId == surahId }
    }

    fun selectActiveSurah(surah: SurahMetadata) {
        selectedSurah = surah
        quranErrorMsg = null
        currentSurahVerses = emptyList()
        activeVerseHighlight = 0
        activePlayingAyahNumber = null
        stopQuranAudioPlayer()
        
        viewModelScope.launch {
            isQuranLoading = true
            try {
                var localDownloaded: DownloadedSurah? = null
                withContext(Dispatchers.IO) {
                    localDownloaded = allDownloadedSurahs.value.find { it.surahId == surah.id }
                }
                
                if (localDownloaded != null) {
                    val ayahs = withContext(Dispatchers.Default) {
                        val arr = JSONArray(localDownloaded!!.versesJson)
                        val list = mutableListOf<Ayah>()
                        for (i in 0 until arr.length()) {
                            val obj = arr.getJSONObject(i)
                            list.add(
                                Ayah(
                                    number = obj.getInt("num"),
                                    textArabic = obj.getString("textAr"),
                                    textEnglish = obj.getString("textEn"),
                                    tafsir = obj.getString("tafsir"),
                                    globalNumber = obj.getInt("globalNum"),
                                    pageNumber = obj.getInt("pageNum")
                                )
                            )
                        }
                        list
                    }
                    currentSurahVerses = ayahs
                } else {
                    val ayahs = com.example.network.QuranApiClient.fetchSurahVerses(surah.id)
                    currentSurahVerses = ayahs
                }
                saveLastReadPosition(surah.id, surah.nameEnglish, 1)
            } catch (e: Exception) {
                currentSurahVerses = QuranData.getFallbackVersesForSurah(surah.id)
                quranErrorMsg = "عذراً، تعذر الوصول لقاعدة البيانات الحية. جرى تشغيل السورة بجلب نسختها الاحتياطية."
            } finally {
                isQuranLoading = false
            }
        }
    }

    fun bookmarkAyah(ayahNumber: Int) {
        val surah = selectedSurah ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val list = allBookmarks.value
            val exists = list.any { it.surahNumber == surah.id && it.ayahNumber == ayahNumber }
            if (exists) {
                repository.deleteBookmark(surah.id, ayahNumber)
            } else {
                val newB = QuranBookmark(surahNumber = surah.id, surahName = surah.nameEnglish, ayahNumber = ayahNumber)
                repository.insertBookmark(newB)
                addUserPoints(20)
            }
        }
    }

    fun isBookmarkedLocally(surahId: Int, ayahNumber: Int): Boolean {
        return allBookmarks.value.any { it.surahNumber == surahId && it.ayahNumber == ayahNumber }
    }

    fun addNoteToAyah(ayahNumber: Int, text: String) {
        val surah = selectedSurah ?: return
        if (text.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            val note = QuranNote(surahNumber = surah.id, surahName = surah.nameEnglish, ayahNumber = ayahNumber, noteText = text)
            repository.insertNote(note)
            addUserPoints(15)
        }
    }

    fun deleteNote(noteId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNoteById(noteId)
        }
    }

    // Real Online Audio Playback via MediaPlayer Stream
    fun playAyahAudio(ayah: Ayah) {
        stopQuranAudioPlayer()
        
        val reciterSlug = when (selectedReciter) {
            "Mishary Al-Afasy" -> "ar.alafasy"
            "Abdul Basit" -> "ar.abdulbasitmurattal"
            "Al-Ghamdi" -> "ar.saadalghamdi"
            else -> "ar.alafasy"
        }
        
        // globalNumber from api response, otherwise simple fallback estimate
        val globalNo = if (ayah.globalNumber > 0) ayah.globalNumber else ayah.number
        val audioUrl = "https://cdn.islamic.network/quran/audio/128/$reciterSlug/$globalNo.mp3"
        
        activePlayingAyahNumber = ayah.number
        activeVerseHighlight = ayah.number
        isAudioPlaying = true
        
        try {
            quranMediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
                prepareAsync()
                setOnPreparedListener { mp ->
                    if (isAudioPlaying) mp.start()
                }
                setOnCompletionListener {
                    // Auto-advance to next Ayah!
                    val nextIndex = currentSurahVerses.indexOfFirst { it.number == ayah.number } + 1
                    if (nextIndex in currentSurahVerses.indices) {
                        playAyahAudio(currentSurahVerses[nextIndex])
                    } else {
                        stopQuranAudioPlayer()
                    }
                }
                setOnErrorListener { _, _, _ ->
                    stopQuranAudioPlayer()
                    true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stopQuranAudioPlayer()
        }
    }

    fun toggleQuranAudio() {
        if (isAudioPlaying) {
            stopQuranAudioPlayer()
        } else {
            val toPlay = currentSurahVerses.find { it.number == (activePlayingAyahNumber ?: 1) } 
                ?: currentSurahVerses.firstOrNull()
            if (toPlay != null) {
                playAyahAudio(toPlay)
            }
        }
    }

    fun stopQuranAudioPlayer() {
        isAudioPlaying = false
        activePlayingAyahNumber = null
        try {
            quranMediaPlayer?.stop()
            quranMediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            quranMediaPlayer = null
        }
    }

    // --- AZKAR INTERACTIONS ---
    fun getRemainingAzkarCount(zikr: Zikr): Int {
        return azkarCountsState[zikr.id] ?: zikr.targetCount
    }

    fun decrementZikrCount(zikr: Zikr) {
        val current = getRemainingAzkarCount(zikr)
        if (current > 0) {
            val next = current - 1
            azkarCountsState[zikr.id] = next
            if (next == 0) {
                // Completed!
                viewModelScope.launch(Dispatchers.IO) {
                    addUserPoints(10) // Completed Zikr gives +10 points
                }
            }
        }
    }

    fun resetZikrProgress() {
        azkarCountsState.clear()
    }

    fun resetTasbih() {
        resetTasbihCount()
    }

    // --- CHAT WITH AI ASSISTANT ---
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(isUser = true, text = text)
        aiChatHistory.add(userMsg)
        aiApiResponseLoading = true
        assistantTypingText = "..."

        viewModelScope.launch(Dispatchers.IO) {
            // Map our chat history to Gemini formats
            val historyConvert = aiChatHistory.dropLast(1).map {
                GeminiContent(parts = listOf(GeminiPart(text = it.text)))
            }
            val reply = GeminiManager.askAssistant(text, historyConvert)

            viewModelScope.launch(Dispatchers.Main) {
                aiApiResponseLoading = false
                assistantTypingText = ""
                aiChatHistory.add(ChatMessage(isUser = false, text = reply))
                addUserPoints(5) // Asking AI encourages knowledge seeking! +5 points
            }
        }
    }

    // Clear whole chat history
    fun clearChat() {
        aiChatHistory.clear()
        aiChatHistory.add(
            ChatMessage(
                isUser = false,
                text = "سجل المحادثة أفرغ بنجاح. أنا هنا في خدمتك!\n\nChat is cleared. Feel free to ask more queries!"
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        compassJob?.cancel()
        playbackJob?.cancel()
    }
}
