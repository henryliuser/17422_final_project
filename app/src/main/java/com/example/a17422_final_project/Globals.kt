package com.example.a17422_final_project

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.TreeMap

/// TODO: json serialization code, use JSONArray
/// TODO: write on quit instead of with every arm/disarm?

class Globals {
    companion object {
        var alarms = TreeMap<String, Alarm>()  // deterministic alphabetical order

        fun writeAlarms(ctx : Context) {
            var globj = JSONObject()
            alarms.forEach { (key, alarm) ->
                globj.put(key, alarm.toJSON())
            }
            val f = File(ctx.filesDir, "alarms.json")
            val userString: String = globj.toString()
            val fileWriter = FileWriter(f)
            val bufferedWriter = BufferedWriter(fileWriter)
            bufferedWriter.write(userString)
            bufferedWriter.close()
            Log.d("writeAlarms", alarms.toString())
        }

        fun readAlarms(ctx : Context) {
            val f = File(ctx.filesDir, "alarms.json")
            if (!f.exists()) return
            val fileReader = FileReader(f)
            val fin = BufferedReader(fileReader)
            var sb = StringBuilder()
            fin.readLines().forEach { s ->
                sb.append(s)
            }
            var globj = JSONObject(sb.toString())
            globj.keys().forEach { s ->
                var obj = globj.getJSONObject(s)
                val a = Alarm.fromJSON(obj)
                alarms[a.name] = a
            }
            Log.d("readAlarms", alarms.toString())
            logAlarms()
        }

        fun logAlarms() {
            alarms.forEach { (k,v) ->
                Log.d("alarm", k + ": {${Integer.toBinaryString(v.mask)} / ${v.hour}:${v.minute}}")
            }
        }
    }
}