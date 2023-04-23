package com.example.a17422_final_project

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity

enum class Task(val task_id: Int) {
    STEPS(0),
    SPEECH(1),
}

fun launchTask(ctx : Context, t : Task) {
    if (t == Task.STEPS)
        ctx.startActivity(Intent(ctx, StepActivity::class.java))
    if (t == Task.SPEECH)
        ctx.startActivity(Intent(ctx, SpeechTask::class.java))
}

fun launchChainedTask(tasks : ArrayList<Task>) {
       
}
