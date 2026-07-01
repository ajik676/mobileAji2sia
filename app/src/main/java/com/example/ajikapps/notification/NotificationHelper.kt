package com.example.ajikapps.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.ajikapps.BaseActivity

object NotificationHelper {

    private const val INSTANT_CHANNEL_ID = "instant_letter_notifications_v2"
    private const val REMINDER_CHANNEL_ID = "reminder_letter_notifications_v2"

    /**
     * Shows an immediate notification as a Heads-Up Notification (pop up from the top).
     */
    fun showInstantNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createChannels(notificationManager)

        val pendingIntent = createPendingIntent(context)

        val builder = NotificationCompat.Builder(context, INSTANT_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set high priority
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Enable sound and vibration
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, builder.build())
    }

    /**
     * Shows a scheduled reminder notification as a Heads-Up Notification (pop up from the top).
     */
    fun showReminderNotification(context: Context, letterTitle: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createChannels(notificationManager)

        val pendingIntent = createPendingIntent(context)

        val builder = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🔔 Pengingat Status Surat")
            .setContentText("Saatnya mengecek status pengajuan surat '$letterTitle' Anda.")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set high priority
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Enable sound and vibration
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, builder.build())
    }

    private fun createChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel 1: Instant Notification (High Importance for Heads-up display)
            val instantChannel = NotificationChannel(
                INSTANT_CHANNEL_ID,
                "Notifikasi Instan Pengajuan",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi langsung saat pengajuan surat desa berhasil dikirim"
                enableLights(true)
                enableVibration(true)
            }

            // Channel 2: Reminder Notification (High Importance for Heads-up display)
            val reminderChannel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "Notifikasi Pengingat Status",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi pengingat untuk mengecek status surat desa Anda"
                enableLights(true)
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(instantChannel)
            notificationManager.createNotificationChannel(reminderChannel)
        }
    }

    private fun createPendingIntent(context: Context): PendingIntent {
        val launchIntent = Intent(context, BaseActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_tab", "list")
        }
        return PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
