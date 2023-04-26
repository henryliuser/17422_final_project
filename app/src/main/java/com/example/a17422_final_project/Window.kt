package com.example.a17422_final_project

import android.content.Context
import android.graphics.PixelFormat
import android.media.AudioAttributes
import android.media.AudioAttributes.USAGE_ALARM
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat.getSystemService
import org.json.JSONObject


class Window(  // declaring required variables
    private val context: Context
) {
    private var mView: View?
//    private var mViewGroup: ViewGroup
    private var mParams: WindowManager.LayoutParams? = null
    private val mWindowManager: WindowManager
    private val layoutInflater: LayoutInflater
    private val powerManager : PowerManager
    private val wakeLock : PowerManager.WakeLock
    var mPlayer: MediaPlayer

    init {
        powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "alarm:turn the screen on")
        val layoutType =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_TOAST
        // set the layout parameters of the window

        mParams = WindowManager.LayoutParams( // Shrink the window to wrap the content rather
            // than filling the screen
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,  // Display it on top of other application windows
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  // Don't let it grab the input focus
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON,

            PixelFormat.TRANSLUCENT  // Make the underlying application window visible through any transparent parts
        )

//        mViewGroup = LinearLayout(this)
        // getting a LayoutInflater
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // inflating the view with the custom layout we created
        mView = layoutInflater.inflate(R.layout.alarm, null)
        // set onClickListener on the remove button, which removes
        // the view from the window
        mView!!.findViewById<View>(R.id.window_close).setOnClickListener { close() }
        // Define the position of the
        // window within the screen
        mParams!!.gravity = Gravity.CENTER
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mPlayer = MediaPlayer.create(
            context,
            R.raw.alarm1,
            AudioAttributes.Builder().
                setUsage(USAGE_ALARM).
                build(),
            audioManager.generateAudioSessionId()
        )
        mPlayer.isLooping = true

    }

    fun open() {
        Log.d("asd", "open")
        wakeLock.acquire(300000)
        try {

            if (mView == null) {
                // inflating the view with the custom layout we created
                mView = layoutInflater.inflate(R.layout.alarm, null)
                // set onClickListener on the remove button, which removes
                // the view from the window
                mView!!.findViewById<View>(R.id.window_close).setOnClickListener { close() }
            }
            // check if the view is already
            // inflated or present in the window
            if (mView!!.windowToken == null) {
                if (mView!!.parent == null) {
                    mWindowManager.addView(mView, mParams)
                }
            }
            mPlayer.start()
        } catch (e: Exception) {
            Log.d("Error1", e.toString())
        }
    }

    fun close() {
        if (wakeLock.isHeld)
            wakeLock.release()
        try {
            Log.d("asd", "close")
            // remove the view from the window
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(mView)
            // invalidate the view
            mView!!.invalidate()
            mView = null
            mPlayer.stop()

            /// TODO: lookup alarm, via intent, and ring it
            /// TODO: loop this activity https://stackoverflow.com/questions/7407242/how-to-cancel-handler-postdelayed

        } catch (e: Exception) {
            Log.d("Error2", e.toString())
        }
    }
}