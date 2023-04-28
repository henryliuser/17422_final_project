package com.example.a17422_final_project

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.ui.AppBarConfiguration
import com.example.a17422_final_project.databinding.ActivityScanBinding
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutionException


class ScanActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityScanBinding
    private val PERMISSION_REQUEST_CAMERA = 0

    private var previewView: PreviewView? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null

    private lateinit var qrCodeFoundButton: Button
    private var qrCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        previewView = findViewById(R.id.activity_main_previewView)

        qrCodeFoundButton = findViewById(R.id.activity_main_qrCodeFoundButton)
        qrCodeFoundButton.visibility = View.INVISIBLE
        qrCodeFoundButton.setOnClickListener(View.OnClickListener {
            Toast.makeText(applicationContext, qrCode, Toast.LENGTH_SHORT).show()
            Log.d(MainActivity::class.java.simpleName, "QR Code Found: $qrCode")
        })

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        requestCamera()


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
            .build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView!!.createSurfaceProvider())
        Log.d("test", "bindCameraPreview")

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(this),
            QRCodeImageAnalyzer(object : QRCodeFoundListener {
                override fun onQRCodeFound(_qrCode: String?) {
                    Log.d("test", "qr code found")
                    qrCode = _qrCode
                    qrCodeFoundButton.visibility = View.VISIBLE
                }

                override fun qrCodeNotFound() {
                    Log.d("test", "qr code not found")
                    qrCodeFoundButton.visibility = View.INVISIBLE
                }
            })
        )
        Log.d("test", "after image analyzer created")
        val camera: Camera =
            cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
    }
}