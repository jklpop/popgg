package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AthanBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("PRAYER_NAME") ?: return
        val muezzin = intent.getStringExtra("MUEZZIN") ?: "أذان مكة"
        
        // Load preferences directly to inspect silent-mode or individual prayer overrides
        val sharedPrefs = context.getSharedPreferences("noor_smart_tasbih_prefs", Context.MODE_PRIVATE)
        val silentMode = sharedPrefs.getBoolean("azan_silent_mode", false)
        val isEnabled = sharedPrefs.getBoolean("azan_enabled_$prayerName", true)
        
        if (silentMode || !isEnabled) return

        val serviceIntent = Intent(context, AthanService::class.java).apply {
            putExtra("PRAYER_NAME", prayerName)
            putExtra("MUEZZIN", muezzin)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
