package com.example.memories

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class RebootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("broadcast ", "receive")

        if(context == null) return

        createNotificationReminder(context)
    }

     fun createNotificationReminder(context: Context){
        var builder = NotificationCompat.Builder(context, "NOTIFICATION_REMINDER_TO_PLAY")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Venez jouer !")
            .setContentText("Faites une partie de memory avec l'application Memories")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(10, builder.build())
        }
    }
    /*
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notif Channel"
            val descriptionText = "Channel use for notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("NOTIFICATION_REMINDER_TO_PLAY", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }*/

}
