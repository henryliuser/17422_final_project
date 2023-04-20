package com.example.a17422_final_project

import android.content.Context
import android.graphics.PixelFormat
import android.media.AudioAttributes
import android.media.AudioAttributes.USAGE_ALARM
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat.getSystemService


class Window(  // declaring required variables
    private val context: Context
) {
    private var mView: View?
//    private var mViewGroup: ViewGroup
    private var mParams: WindowManager.LayoutParams? = null
    private val mWindowManager: WindowManager
    private val layoutInflater: LayoutInflater
    var mPlayer: MediaPlayer

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            mParams = WindowManager.LayoutParams( // Shrink the window to wrap the content rather
                // than filling the screen
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,  // Display it on top of other application windows
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // Don't let it grab the input focus
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  // Make the underlying application window visible
                // through any transparent parts
                PixelFormat.TRANSLUCENT
            )
        }
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
        try {
            Log.d("asd", "close")
            // remove the view from the window
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(mView)
            // invalidate the view
            mView!!.invalidate()
            mView = null
            mPlayer.stop()
            // remove all views
//            Log.d("asd", mView.toString())
//            Log.d("asd", mView!!.parent.toString())
//            (mView!!.parent as ViewGroup).removeAllViews()
//            mView = null

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (e: Exception) {
            Log.d("Error2", e.toString())
        }
    }
}