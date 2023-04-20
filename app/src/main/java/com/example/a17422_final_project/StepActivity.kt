package com.example.a17422_final_project

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.a17422_final_project.databinding.ActivityStepBinding


class StepActivity : AppCompatActivity() {

    private lateinit var mAccel : Accelerometer
    private lateinit var binding: ActivityStepBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStepBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAccel = Accelerometer(this)

        // create a listener for accelerometer
        mAccel.setListener(object : Accelerometer.Listener {
            //on translation method of accelerometer
            override fun onTranslation(tx: Float, ty: Float, ts: Float) {
                // set the color red if the device moves in positive x axis
                Log.d("accelerometer", "[%6.3f, %6.3f, %6.3f]".format(tx, ty, ts))
                if (tx > 1.0f) {
                    window.decorView.setBackgroundColor(Color.RED)
                } else if (tx < -1.0f) {
                    window.decorView.setBackgroundColor(Color.BLUE)
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