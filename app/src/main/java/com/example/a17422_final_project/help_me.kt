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

fun getIntent(ctx : Context, t : TaskType, params : Bundle?): Intent {
    val intent = when (t) {
        TaskType.STEPS  -> Intent(ctx, StepActivity::class.java)
        TaskType.SPEECH -> Intent(ctx, SpeechTask::class.java)
    }
    return intent.putExtra("params", params)
}

fun startTaskStack(ctx : Context, tasks : Array<TaskType>, params : Array<Bundle?>) {
    val tsb = TaskStackBuilder.create(ctx)
    (tasks zip params).forEach { (t, p) ->
        tsb.addNextIntent( getIntent(ctx, t, p) )
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
