package com.example.data

import kotlinx.coroutines.flow.Flow

class NoorRepository(private val noorDao: NoorDao) {

    val allPrayerTracks: Flow<List<PrayerTrack>> = noorDao.getAllPrayerTracks()
    val allBookmarks: Flow<List<QuranBookmark>> = noorDao.getAllBookmarks()
    val allNotes: Flow<List<QuranNote>> = noorDao.getAllNotes()
    val userProgress: Flow<UserProgress?> = noorDao.getUserProgress()

    suspend fun getPrayerTrackForDate(date: String): PrayerTrack? {
        return noorDao.getPrayerTrackForDate(date)
    }

    suspend fun insertPrayerTrack(track: PrayerTrack) {
        noorDao.insertPrayerTrack(track)
    }

    fun isBookmarked(surahNum: Int, ayahNum: Int): Flow<Boolean> {
        return noorDao.isBookmarked(surahNum, ayahNum)
    }

    suspend fun insertBookmark(bookmark: QuranBookmark) {
        noorDao.insertBookmark(bookmark)
    }

    suspend fun deleteBookmark(surahNum: Int, ayahNum: Int) {
        noorDao.deleteBookmark(surahNum, ayahNum)
    }

    suspend fun insertNote(note: QuranNote) {
        noorDao.insertNote(note)
    }

    suspend fun deleteNoteById(id: Int) {
        noorDao.deleteNoteById(id)
    }

    suspend fun insertUserProgress(progress: UserProgress) {
        noorDao.insertUserProgress(progress)
    }

    // --- Downloaded Surahs (Offline Quran Support) ---
    val allDownloadedSurahs: Flow<List<DownloadedSurah>> = noorDao.getAllDownloadedSurahs()

    suspend fun isSurahDownloaded(surahId: Int): Boolean {
        return noorDao.isSurahDownloaded(surahId)
    }

    suspend fun insertDownloadedSurah(surah: DownloadedSurah) {
        noorDao.insertDownloadedSurah(surah)
    }

    suspend fun deleteDownloadedSurah(surahId: Int) {
        noorDao.deleteDownloadedSurah(surahId)
    }
}
