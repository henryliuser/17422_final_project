package com.example.a17422_final_project

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter


class Alarm {
    var name   : String = ""
    var mask   : Int = 0
    var hour   : Int = 0
    var minute : Int = 0
}

class Globals {
    companion object {
        var alarms = HashMap<String, Alarm>()

        fun writeAlarms(ctx : Context) {
            var globj = JSONObject()
            alarms.forEach { (key, alarm) ->
                var obj = JSONObject()
                obj.put("name", alarm.name)
                obj.put("mask", alarm.mask)
                obj.put("hour", alarm.hour)
                obj.put("minute", alarm.minute)
                globj.put(key, obj)
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
            var lines = fin.readLines()
            lines.forEach { s ->
                sb.append(s)
            }
            var globj = JSONObject(sb.toString())
            globj.keys().forEach { s ->
                var obj = globj.get(s) as JSONObject
                var a = Alarm()
                a.name = obj.get("name") as String
                a.mask = obj.get("mask") as Int
                a.hour = obj.get("hour") as Int
                a.minute = obj.get("minute") as Int
                alarms.put(a.name, a)
            }
            Log.d("readAlarms", alarms.toString())
        }

        fun setAlarms() {

        }
    }
}