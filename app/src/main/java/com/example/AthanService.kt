package com.example

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.io.IOException

class AthanService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == "STOP_ATHAN") {
            stopAthan()
            stopSelf()
            return START_NOT_STICKY
        }

        val prayerName = intent?.getStringExtra("PRAYER_NAME") ?: "الصلاة"
        val muezzin = intent?.getStringExtra("MUEZZIN") ?: "أذان مكة"
        
        playAthan(prayerName, muezzin)
        return START_STICKY
    }

    private fun playAthan(prayerName: String, muezzin: String) {
        stopAthan()

        val channelId = "noor_athan_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "أوقات الأذان والصلوات",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "قناة لتشغيل الأذان ودخول أوقات الصلاة"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val stopIntent = Intent(this, AthanService::class.java).apply {
            this.action = "STOP_ATHAN"
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 202, stopIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Notification category alarm and action to dismiss
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("حان الآن وقت صلاة $prayerName")
            .setContentText("صوت $muezzin")
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .addAction(android.R.drawable.ic_media_pause, "إيقاف الأذان", stopPendingIntent)
            .setAutoCancel(true)
            .build()

        startForeground(101, notification)

        // Real streaming links
        val url = when {
            muezzin.contains("المدينة") -> "https://download.quranicaudio.com/adhan/madinah.mp3"
            muezzin.contains("الأقصى") -> "https://download.quranicaudio.com/adhan/alaqsa.mp3"
            muezzin.contains("القاهرة") -> "https://download.quranicaudio.com/adhan/abdul_basit.mp3"
            else -> "https://download.quranicaudio.com/adhan/makkah.mp3"
        }

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            )
            try {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { player ->
                    player.setVolume(0.0f, 0.0f)
                    player.start()
                    // Progressive Volume Fade In (over 4 seconds)
                    serviceScope.launch {
                        for (i in 1..20) {
                            delay(200)
                            val vol = i / 20.0f
                            player.setVolume(vol, vol)
                        }
                    }
                }
                setOnCompletionListener {
                    stopSelf()
                }
                setOnErrorListener { _, _, _ ->
                    stopSelf()
                    false
                }
            } catch (e: Exception) {
                stopSelf()
            }
        }
    }

    private fun stopAthan() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAthan()
        serviceJob.cancel()
    }
}
