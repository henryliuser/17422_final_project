package com.example.a17422_final_project

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.a17422_final_project.databinding.ActivityAlarmsetBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class ActivityAlarmSet : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmsetBinding
    private lateinit var nameEdit : EditText

    private var weekdayMask : Int = 0
    private var hr : Int = 0
    private var min : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmsetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nameEdit = findViewById<EditText>(R.id.editName)

        findViewById<Button>(R.id.editTime)
            .setOnClickListener {
                AlarmSetter(::updateTime).show(supportFragmentManager, "alarmsetter")
            }

        findViewById<CheckBox>(R.id.mon).setOnCheckedChangeListener(updateMask(0))
        findViewById<CheckBox>(R.id.tue).setOnCheckedChangeListener(updateMask(1))
        findViewById<CheckBox>(R.id.wed).setOnCheckedChangeListener(updateMask(2))
        findViewById<CheckBox>(R.id.thu).setOnCheckedChangeListener(updateMask(3))
        findViewById<CheckBox>(R.id.fri).setOnCheckedChangeListener(updateMask(4))
        findViewById<CheckBox>(R.id.sat).setOnCheckedChangeListener(updateMask(5))
        findViewById<CheckBox>(R.id.sun).setOnCheckedChangeListener(updateMask(6))


        findViewById<Button>(R.id.saveAlarm).setOnClickListener { save() }

    }

    fun updateMask(day : Int): (CompoundButton, Boolean) -> Unit {
        return { buttonView : CompoundButton, isChecked : Boolean ->
            val yes = if (isChecked) 1 else 0
            weekdayMask = weekdayMask and (1 shl day).inv()
            weekdayMask = weekdayMask or (yes shl day)
            Log.d("updateMask", Integer.toBinaryString(weekdayMask).toString())
        }
    }

    fun updateTime(hour : Int, minute : Int) {
        hr = hour
        min = minute
        Log.d("updateTime", "$hr : $min")
    }

    fun save() {
        // write to json
//        val name : String = nameEdit.text.toString()
//        if (Globals.alarms.containsKey(name)) {
//            val msg = Snackbar.make(binding.root, "Please choose a unique name", Snackbar.LENGTH_SHORT)
//            msg.show()
//            return
//        }
//        var a = Alarm()
//        a.name = name
//        a.mask = weekdayMask
//        a.hour = hr
//        a.minute = min
//        Globals.alarms[name] = a
//        Globals.writeAlarms(applicationContext)  // O(N) write every time, whatever. seems safer than write on exit.
    }
}