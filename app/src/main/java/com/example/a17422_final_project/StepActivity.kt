package com.example.a17422_final_project

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.a17422_final_project.databinding.ActivityStepBinding


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

        mAccel = Accelerometer(this)
        val progressBar : ProgressBar = findViewById(R.id.progressBar)
        progressBar.max = 10 // TODO: set this to change based on the number of steps that the user wants to take

        // create a listener for accelerometer
        mAccel.setListener(object : Accelerometer.Listener {
            //on translation method of accelerometer
//            override fun onTranslation(tx: Float, ty: Float, ts: Float) {
//                // set the color red if the device moves in positive x axis
//                Log.d("accelerometer", "[%6.3f, %6.3f, %6.3f]".format(tx, ty, ts))
//                if (tx > 1.0f) {
//                    window.decorView.setBackgroundColor(Color.RED)
//                } else if (tx < -1.0f) {
//                    window.decorView.setBackgroundColor(Color.BLUE)
//                }
//            }
            override fun onTranslation() {
                steps++
                val tv1: TextView = findViewById(R.id.stepCount)
                tv1.text = "Number of Steps Taken:" + steps.toString()
                Log.d("step", steps.toString())
                progressBar.progress = steps

                if (steps == progressBar.max) {
                    // TODO: fix this part to call the next activity that we want
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

//    override fun onBackInvoke() {
//        super.onBackPressed()
//    }
}