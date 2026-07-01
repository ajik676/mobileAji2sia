package com.example.ajikapps.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val letterTitle = intent.getStringExtra("letter_title") ?: "Surat Desa"
        NotificationHelper.showReminderNotification(context, letterTitle)
    }
}
