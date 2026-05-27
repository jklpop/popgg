package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_tracks")
data class PrayerTrack(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateString: String, // format: YYYY-MM-DD
    val fajr: Boolean = false,
    val dhuhr: Boolean = false,
    val asr: Boolean = false,
    val maghrib: Boolean = false,
    val isha: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quran_bookmarks")
data class QuranBookmark(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quran_notes")
data class QuranNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val noteText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1, // Single row config
    val points: Int = 0,
    val level: Int = 1,
    val streakDays: Int = 0,
    val lastActiveDate: String = "" // format: YYYY-MM-DD
)

@Entity(tableName = "downloaded_surahs")
data class DownloadedSurah(
    @PrimaryKey val surahId: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val revelationType: String,
    val versesCount: Int,
    val versesJson: String, // Serialized List<Ayah>
    val timestamp: Long = System.currentTimeMillis()
)
