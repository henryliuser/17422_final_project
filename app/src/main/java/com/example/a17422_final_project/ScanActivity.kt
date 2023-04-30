package com.example.a17422_final_project

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.Surface.ROTATION_90
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraX
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.ui.AppBarConfiguration
import com.example.a17422_final_project.databinding.ActivityScanBinding
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.common.util.concurrent.ListenableFuture
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.ExecutionException


class ScanActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityScanBinding
    private val PERMISSION_REQUEST_CAMERA = 0

    private var previewView: PreviewView? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null


    private lateinit var qrCodeFoundButton: Button
    private var qrCode: String? = null
    private lateinit var camera : Camera

    private lateinit var analyzer : QRCodeImageAnalyzer

    private lateinit var current : TextView
    private lateinit var time : TextView

    private var activities = ArrayList<String>()
    private var times = ArrayList<Int>()

    private var scanIdx = 0
    private lateinit var initialTime : LocalTime
    private var targetTimes = ArrayList<LocalTime>()
    var timeLeft = 0
    var handler = Handler(Looper.getMainLooper())

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // populate the activities/times
        activities = arrayListOf(
            "Shower",
            "Breakfast",
            "Exercise",
        )
        times = arrayListOf(
            10,
            5,
            5,
        )

        current = findViewById(R.id.current)
        time = findViewById(R.id.time)

        previewView = findViewById(R.id.activity_main_previewView)

        qrCodeFoundButton = findViewById(R.id.activity_main_qrCodeFoundButton)
        qrCodeFoundButton.visibility = View.INVISIBLE
        qrCodeFoundButton.setOnClickListener(View.OnClickListener {
            Toast.makeText(applicationContext, qrCode, Toast.LENGTH_SHORT).show()
            Log.d(MainActivity::class.java.simpleName, "QR Code Found: $qrCode")
        })

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        analyzer = QRCodeImageAnalyzer(object : QRCodeFoundListener {
            override fun onQRCodeFound(_qrCode: String?) {
                Log.d("test", "qr code found: $_qrCode")
                qrCode = _qrCode
                if (scanIdx == 0) {
                    initialTime = LocalTime.now()

                    var cur = Duration.ZERO
                    times.forEach {
                        val x = Duration.ofSeconds(it.toLong())
                        targetTimes.add(initialTime.plus(cur).plus(x))
                        cur += x
                    }
                    current.text = "Current:\n${activities.removeFirst()}"
                    timeLeft = times.removeFirst()
                    time.text = "Time Left:\n${timeLeft}"

                }

//                Log.d("")
                if (scanIdx > 0 && activities.isNotEmpty() && _qrCode == activities.first()) {
                    advance()
                }
                scanIdx++

                qrCodeFoundButton.visibility = View.VISIBLE
            }

            override fun qrCodeNotFound() {
                Log.d("test", "qr code not found")
                qrCodeFoundButton.visibility = View.INVISIBLE
            }
        }, this)

        requestCamera()


        fun updateTimer() {
            if (timeLeft > 0) timeLeft--
            time.text = "Time Left:\n${timeLeft}"
            if (activities.isEmpty() && timeLeft == 0) {
                current.text = "Done!"
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed( {
                    finish()
                }, 1000)
                return
            }
            handler.postDelayed( {
                updateTimer()

            }, 1000)
        }
        updateTimer()


//        setSupportActionBar(binding.toolbar)
//
//        val navController = findNavController(R.id.nav_host_fragment_content_scan)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
    }

    private fun requestCamera() {

        // request permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf<String>(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        } else {
            Log.d("test", "already had permission to use camera")
            startCamera()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun advance() {
        Log.d("advance", "hello")
        val act = activities.removeFirst()
        val t = times.removeFirst()
        val target = targetTimes.first()
        targetTimes.removeFirst()


        if (activities.isEmpty()) {
            current.text = "Done!"
            Handler(Looper.getMainLooper()).postDelayed( {
                finish()
            }, 1000)
            return
        }

        current.text = "Current:\n${act}"
        val now = LocalTime.now()
        if (now > target) {
            activities.removeLast()
            val end = targetTimes.last()
            timeLeft = Duration.between(now, end).seconds.toInt()
            time.text = "Time Left:\n${timeLeft} seconds"
        }
        else {  // on time
            timeLeft = Duration.between(target,now).seconds.toInt()
            time.text = "Time Left:\n${timeLeft} seconds"
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
                Log.d("test", "start camera called")
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        cameraProviderFuture!!.addListener({
            try {
                val cameraProvider = cameraProviderFuture!!.get()
                bindCameraPreview(cameraProvider)
            } catch (e: ExecutionException) {
                Toast.makeText(this, "Error starting camera ", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: InterruptedException) {
                Toast.makeText(this, "Error starting camera ", Toast.LENGTH_SHORT)
                    .show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraPreview(cameraProvider: ProcessCameraProvider) {
        previewView!!.preferredImplementationMode = PreviewView.ImplementationMode.SURFACE_VIEW
        val preview: Preview = Preview.Builder()
            .setTargetResolution(Size(200, 200))
            .build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView!!.createSurfaceProvider())
        Log.d("test", "bindCameraPreview")

        val imageAnalysis : ImageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(200, 200))
            .setTargetRotation(ROTATION_90)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), analyzer)
        Log.d("test", "after image analyzer created")
        camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, imageAnalysis, preview)
    }
}