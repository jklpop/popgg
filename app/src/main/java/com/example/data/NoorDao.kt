package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoorDao {

    // --- Prayer Track ---
    @Query("SELECT * FROM prayer_tracks ORDER BY dateString DESC")
    fun getAllPrayerTracks(): Flow<List<PrayerTrack>>

    @Query("SELECT * FROM prayer_tracks WHERE dateString = :date LIMIT 1")
    suspend fun getPrayerTrackForDate(date: String): PrayerTrack?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerTrack(track: PrayerTrack)

    // --- Quran Bookmarks ---
    @Query("SELECT * FROM quran_bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<QuranBookmark>>

    @Query("SELECT EXISTS(SELECT 1 FROM quran_bookmarks WHERE surahNumber = :surahNum AND ayahNumber = :ayahNum)")
    fun isBookmarked(surahNum: Int, ayahNum: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: QuranBookmark)

    @Query("DELETE FROM quran_bookmarks WHERE surahNumber = :surahNum AND ayahNumber = :ayahNum")
    suspend fun deleteBookmark(surahNum: Int, ayahNum: Int)

    // --- Quran Notes ---
    @Query("SELECT * FROM quran_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<QuranNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: QuranNote)

    @Query("DELETE FROM quran_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)

    // --- User Progress (Gamification) ---
    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    fun getUserProgress(): Flow<UserProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgress(progress: UserProgress)

    // --- Downloaded Surahs (Offline Quran Support) ---
    @Query("SELECT * FROM downloaded_surahs ORDER BY timestamp DESC")
    fun getAllDownloadedSurahs(): Flow<List<DownloadedSurah>>

    @Query("SELECT EXISTS(SELECT 1 FROM downloaded_surahs WHERE surahId = :surahId)")
    suspend fun isSurahDownloaded(surahId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownloadedSurah(surah: DownloadedSurah)

    @Query("DELETE FROM downloaded_surahs WHERE surahId = :surahId")
    suspend fun deleteDownloadedSurah(surahId: Int)
}
