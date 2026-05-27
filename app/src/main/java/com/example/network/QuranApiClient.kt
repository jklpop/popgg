package com.example.network

import com.example.data.Ayah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object QuranApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    /**
     * Fetches the complete Arabic, English, and Tafsir data for a given Surah number
     * using the AlQuran Cloud multi-edition endpoint.
     */
    suspend fun fetchSurahVerses(surahNumber: Int): List<Ayah> = withContext(Dispatchers.IO) {
        val url = "https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,en.sahih,ar.jalalayn"
        val request = Request.Builder()
            .url(url)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                
                val responseBody = response.body?.string() ?: throw IOException("Empty body response")
                val jsonObject = JSONObject(responseBody)
                
                if (jsonObject.getInt("code") != 200) {
                    throw IOException("API response error code: ${jsonObject.getInt("code")}")
                }
                
                val dataArray = jsonObject.getJSONArray("data")
                if (dataArray.length() < 3) {
                    throw IOException("Incomplete editions array returned")
                }
                
                val dataArabic = dataArray.getJSONObject(0)
                val dataEnglish = dataArray.getJSONObject(1)
                val dataTafsir = dataArray.getJSONObject(2)
                
                val ayahsArabic = dataArabic.getJSONArray("ayahs")
                val ayahsEnglish = dataEnglish.getJSONArray("ayahs")
                val ayahsTafsir = dataTafsir.getJSONArray("ayahs")
                
                val parsedAyahs = mutableListOf<Ayah>()
                val length = ayahsArabic.length()
                
                for (i in 0 until length) {
                    val ayAr = ayahsArabic.getJSONObject(i)
                    val ayEn = ayahsEnglish.getJSONObject(i)
                    val ayTaf = ayahsTafsir.getJSONObject(i)
                    
                    val numInSurah = ayAr.getInt("numberInSurah")
                    val textAr = ayAr.getString("text")
                    val textEn = ayEn.getString("text")
                    val tafAr = ayTaf.getString("text")
                    val globalNum = ayAr.getInt("number")
                    val pageNum = ayAr.optInt("page", 1)
                    
                    parsedAyahs.add(
                        Ayah(
                            number = numInSurah,
                            textArabic = textAr,
                            textEnglish = textEn,
                            tafsir = tafAr,
                            globalNumber = globalNum,
                            pageNumber = pageNum
                        )
                    )
                }
                
                parsedAyahs
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Rethrow or return empty list depends on caller, let's rethrow to handle in UI with specific states
            throw e
        }
    }
}
