package com.example.a17422_final_project

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import org.json.JSONObject

class AlarmHandler : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BUTTONS", "Alarm received")
        var builder = NotificationCompat.Builder(context, "1001")
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("alarm")
            .setContentText("alarm")
            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE) //Important for heads-up notification
            .setPriority(PRIORITY_HIGH); //Important for heads-up notification
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(1001, builder.build())
        }


        // check if the user has already granted
        // the Draw over other apps permission
        ForegroundService.start(context)
    }

}