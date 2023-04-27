package com.example.a17422_final_project

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.a17422_final_project.databinding.ActivityMainBinding
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private fun createNotificationChannel(name: String, desc: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("1001", name, importance).apply {
                description = desc
            }
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Globals.readAlarms(applicationContext)
        /// TODO: uncomment

        createNotificationChannel("alarms", "alarms")
        findViewById<Button>(R.id.button)
            .setOnClickListener {
                Log.d("BUTTONS", "User tapped the Supabutton")
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val calendar = Calendar.getInstance()
                val intent = Intent(this, AlarmHandler::class.java)
                val pendingIntent = PendingIntent.getBroadcast(this, 1001, intent, PendingIntent.FLAG_IMMUTABLE)
                val info = AlarmManager.AlarmClockInfo(calendar.timeInMillis + 10000, pendingIntent)
                val intent2 = Intent(this, AlarmHandler::class.java)
                val pendingIntent2 = PendingIntent.getBroadcast(this, 1001, intent2, PendingIntent.FLAG_IMMUTABLE)

                alarmManager.setAlarmClock(info, pendingIntent2)
            }

        findViewById<Button>(R.id.postDelayed)
            .setOnClickListener {
                Handler(Looper.getMainLooper()).postDelayed({
                    ForegroundService.start(this)
                }, 2000)
            }

        findViewById<Button>(R.id.button3)
            .setOnClickListener {
                startTaskStack(this, arrayListOf( Task(TaskType.STEPS, 3, JSONObject()) ))
//                startActivity(Intent(this, StepActivity::class.java))
                Log.d("after start activity", "step")
            }

        findViewById<Button>(R.id.to_speech)
            .setOnClickListener {
                startTaskStack(this, arrayListOf( Task(TaskType.SPEECH, 3, JSONObject()) ))
//                startActivity(Intent(this, SpeechTask::class.java))
            }


        if (!Settings.canDrawOverlays(this)) {
            val intent =
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent, savedInstanceState)
        }

        findViewById<Button>(R.id.newAlarm)
            .setOnClickListener {
                startActivity(Intent(this, ActivityAlarmSet::class.java))
            }

        findViewById<Button>(R.id.chainTask)
            .setOnClickListener {
                val stepParams = JSONObject()
                stepParams.put("numSteps", 25)
                val tasks = arrayListOf(
                    Task( TaskType.STEPS, 3, stepParams ),
                    Task( TaskType.SPEECH, 15, JSONObject() )
                )
                startTaskStack(this, tasks)
            }
    }


}
