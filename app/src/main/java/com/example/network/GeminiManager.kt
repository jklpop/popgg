package com.example.network

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiManager {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: GeminiApiService by lazy {
        retrofit.create(GeminiApiService::class.java)
    }

    /**
     * Call the Gemini API using the API Key configured via standard secrets.
     * Falls back to a mock/guided response if the API Key is unconfigured, ensuring robust UX.
     */
    suspend fun askAssistant(prompt: String, conversationHistory: List<GeminiContent> = emptyList()): String {
        // Safe check for the API key availability
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return simulateLocalIslamicResponse(prompt)
        }

        val systemPrompt = "You are Noor, a highly respectful, compassionate, and knowledgeable Islamic Assistant. " +
                "You provide authentic information about Quran, Hadith, prayers, and morals in a warm, serene tone. " +
                "Directly support the language in which the user queries (Arabic, English, French, Turkish, Urdu). " +
                "Always conclude with peaceful wisdom."

        val currentContent = GeminiContent(parts = listOf(GeminiPart(text = prompt)))
        val contentsList = conversationHistory + listOf(currentContent)

        val request = GeminiRequest(
            contents = contentsList,
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
        )

        return try {
            val response = apiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Pardon, I could not generate a response. Let us invoke peace upon the Prophet Muhammad."
        } catch (e: Exception) {
            "Connection slow. Let me answer offline:\n\n" + simulateLocalIslamicResponse(prompt)
        }
    }

    private fun simulateLocalIslamicResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return if (lower.contains("hello") || lower.contains("سلام") || lower.contains("أهلاً") || lower.contains("welcome")) {
            "السلام عليكم ورحمة الله وبركاته!\nأهلاً بك في مساعد نور الذكي. أنا هنا لمساعدتك في فهم أمور دينك وتلاوة القرآن والأذكار اليومية.\n\n" +
                    "Peace be upon you! Welcome to Noor AI Assistant. Ask me anything about Quran, Duas, or prayer times."
        } else if (lower.contains("prayer") || lower.contains("صلاة") || lower.contains("أذان")) {
            "الصلاة صلة العبد بربه، وهي عماد الدين. حافظ على الصلوات الخمس في أوقاتها المحددة ونل الفلاح والسكينة. يُفضل الاستعداد قبل الأذان بدقائق ممتلئة بالاستغفار.\n\n" +
                    "Prayer is the fundamental connection between a believer and their Lord. Keep up the five daily prayers on time to receive divine serenity."
        } else if (lower.contains("quran") || lower.contains("قرأن") || lower.contains("آية") || lower.contains("verse")) {
            "لقد تيسر لك تصفح سور القرآن العظيم عبر تطبيق نور. تذكر قول النبي ﷺ: 'اقرؤوا القرآن فإنه يأتي يوم القيامة شفيعاً لأصحابه'. ابدأ الآن بقراءة الفاتحة أو يس وتأمل معاني التفسير المرفقة.\n\n" +
                    "The Noble Quran is guidance and light. The Prophet (PBUH) taught us that reciting even a single letter yields manifold blessings. Try to set a daily goal of 5 verses."
        } else if (lower.contains("azkar") || lower.contains("أذكار") || lower.contains("ذكر") || lower.contains("سبحة")) {
            "الذكر جلاء القلوب وطمأنينتها، لقوله سبحانه: 'ألا بذكرِ الله تطمئن القلوب'. بإمكانك الانتقال لصفحة الأذكار وتبويب السبحة الإلكترونية لتتبع استغفارك وتسبيحك يومياً.\n\n" +
                    "Remembrance of Allah is the healer of hearts. Use the Azkar tab in Noor App to count morning/evening supplications with instant vibrations."
        } else {
            "أكرمك الله ونوّر قلبك بالإيمان! سؤال مبارك وطيب. تذكّر دوماً السعي لتعلم العلم النافع، وبث السلام والتراحم بين الناس، والالتزام ببر الوالدين وحب الخير للجميع.\n\n" +
                    "May Allah bless your noble quest for knowledge. Try to explore Quran translations and keep your streaks high on the Progress screen!"
        }
    }
}
