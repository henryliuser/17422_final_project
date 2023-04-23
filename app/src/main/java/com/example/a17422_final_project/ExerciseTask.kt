package com.example.a17422_final_project

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.a17422_final_project.databinding.TaskExerciseBinding
import java.util.*
import kotlin.random.Random

class ExerciseTask : AppCompatActivity() {

    private lateinit var binding: TaskExerciseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TaskExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // request permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(arrayOf<String>(Manifest.permission.RECORD_AUDIO), 1)
        }

    }
}