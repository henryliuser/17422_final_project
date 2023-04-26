package com.example.a17422_final_project

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.a17422_final_project.databinding.ActivityStepBinding
import org.json.JSONObject


class StepActivity : AppCompatActivity() {

    private lateinit var mAccel : Accelerometer
    private lateinit var binding: ActivityStepBinding
    private var steps : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStepBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d("perms", "activity_recognition")
            requestPermissions(arrayOf<String>(Manifest.permission.ACTIVITY_RECOGNITION), 0)
        }

        val params = JSONObject(intent.getStringExtra("params")!!)
        mAccel = Accelerometer(this)
        val progressBar : ProgressBar = findViewById(R.id.progressBar)
        progressBar.max = 15
        if (params.has("numSteps"))
            progressBar.max = params.getInt("numSteps")

        // create a listener for accelerometer
        mAccel.setListener(object : Accelerometer.Listener {
            override fun onTranslation() {
                steps++
                val tv1: TextView = findViewById(R.id.stepCount)
                tv1.text = "Number of Steps Taken: $steps"
                Log.d("step", steps.toString())
                progressBar.progress = steps

                if (steps >= progressBar.max) {
                    tv1.text = "Done!"
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    }, 1500)
                }

            }
        })
    }
    override fun onResume() {
        super.onResume()
        mAccel.register()
    }

    override fun onPause() {
        super.onPause()
        mAccel.unregister()
    }

}