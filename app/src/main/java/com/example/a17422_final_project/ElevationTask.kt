package com.example.a17422_final_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.a17422_final_project.databinding.TaskElevationBinding

class ElevationTask : AppCompatActivity() {
    private lateinit var binding : TaskElevationBinding

    fun setup() {
        /// TODO: check permissions. also add to manifest
        /// TODO: number of lifts, threshold
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TaskElevationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // perms
    }
}