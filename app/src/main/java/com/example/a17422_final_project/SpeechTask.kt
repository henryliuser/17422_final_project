package com.example.a17422_final_project

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.a17422_final_project.databinding.TaskSpeechBinding
import org.json.JSONObject
import java.util.Locale
import kotlin.random.Random

class SpeechTask : AppCompatActivity(), TaskActivity {

    private val prompts = arrayOf (
        "how much wood would a woodchuck chuck if a woodchuck could chuck wood",
        "peter piper picked a peck of pickled peppers how many pickled peppers did peter piper pick",
        "frivolously fanciful fried fresh fish furiously",
        "which witch switched the swiss wrist watches",
        "she sells seashells by the seashore",
        "a b c d e f g h i j k l m n o p q r s t u v w x y z",
        "a b c d e f g h i j k l m n o p q r s t u v w x y z"
    )

    private lateinit var binding: TaskSpeechBinding
    private lateinit var bot : SpeechRecognizer
    private lateinit var prompt_text : TextView
    private lateinit var user_text : TextView
    private var done = false

    override lateinit var timer : Timer
    override lateinit var params : JSONObject

    fun checkCorrect(s : String, t : String, i : Int) : Boolean {
        val sa = s.split("\\s".toRegex()).toTypedArray()
        val ta = t.split("\\s".toRegex()).toTypedArray()
        if (i == 5) {
            Log.d("checkCorrect", "$sa | $ta")
            return sa.contentEquals(ta)
        }
        if (sa.size != ta.size) return false
        var mismatch = 0
        (sa zip ta).forEach { (a,b) ->
            if (a != b)
                mismatch += 1
        }
        return mismatch <= 1
    }

    override fun getPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TaskSpeechBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init(this, intent)

        done = false

        prompt_text = findViewById(R.id.prompt)
        user_text = findViewById(R.id.result)

        var i = Random.nextInt(7)
        prompt_text.text = prompts[i]
        if (i == 6) {  // shuffled alphabet
            val asd = prompts[i].split("\\s".toRegex()).toTypedArray()
            asd.shuffle()
            prompt_text.text = asd.joinToString(separator = " ")
        }

        bot = SpeechRecognizer.createSpeechRecognizer(this)
        bot.setRecognitionListener(object : RecognitionListener {

            override fun onResults(p0: Bundle?) {
                if (done) return
                val data = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val input = data?.get(0)
                user_text.text = input
                Log.d("speech", "onResults() $input")

                if (checkCorrect(input!!.lowercase(), prompt_text.text.toString(), i)) {
                    done = true
                    prompt_text.text = "Correct!"
                    Handler(Looper.getMainLooper()).postDelayed({
                        destroy()
                        finish()
                    }, 1500)
                }
            }

            override fun onPartialResults(p0: Bundle?) {}
            override fun onEvent(p0: Int, p1: Bundle?) {}
            override fun onReadyForSpeech(p0: Bundle?) {}
            override fun onBeginningOfSpeech() { user_text.text = "Listening..." }
            override fun onRmsChanged(p0: Float) {}
            override fun onBufferReceived(p0: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(p0: Int) {}

        })



        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        findViewById<Button>(R.id.recordButton)
            .setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(p0: View, p1: MotionEvent): Boolean {
                    if (p1.action == MotionEvent.ACTION_UP) {
                        user_text.text = ""
                        bot.stopListening()
                    }
                    if (p1.action == MotionEvent.ACTION_DOWN) {
                        bot.startListening(speechIntent)
                    }
                    return false
                }
            })




    }


}