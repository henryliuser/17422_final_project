package com.example.a17422_final_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat.startActivity


enum class TaskType(val key: Int) {
    STEPS(0),
    SPEECH(1);
    companion object {
        private val map = TaskType.values().associateBy(TaskType::key)
        operator fun get(id: Int) = map[id]!!
    }
}

//interface Task {
//    fun setup()  // initialize with parameters
//    fun teardown()  // task completed
//
//}

fun startTaskStack(ctx : Context, tasks : Iterable<Task>) {
    val tsb = TaskStackBuilder.create(ctx)
    tsb.addNextIntent( Intent(ctx, MainActivity::class.java) )
    tasks.reversed().forEach { t ->
        tsb.addNextIntent( t.makeIntent(ctx) )
    }
    tsb.startActivities()
}

//fun launchNextTask(ctx : Context, tasks : ArrayList<Int>) {
//    val t = tasks.last()
//    tasks.removeLast()
//    val intent = getIntent(ctx, Task[t])
//    intent.putIntegerArrayListExtra("remainingTasks", tasks)
//    ctx.startActivity(intent)
//}
