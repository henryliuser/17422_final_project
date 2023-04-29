package com.example.a17422_final_project

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.view.View
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.lifecycle.Observer
import com.example.a17422_final_project.databinding.TaskExerciseBinding
import com.example.a17422_final_project.helpers.GraphicOverlay
import com.example.a17422_final_project.helpers.PoseDetectorProcessor
import com.example.a17422_final_project.helpers.VisionBaseProcessor
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.common.util.concurrent.ListenableFuture
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.Executors


abstract class VideoHelperActivity : AppCompatActivity(), TaskActivity {

    protected lateinit var previewView: PreviewView
    protected lateinit var graphicOverlay: GraphicOverlay
    private lateinit var outputTextView: TextView
    private lateinit var addFaceButton: ExtendedFloatingActionButton
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private lateinit var processor: VisionBaseProcessor<PoseDetectorProcessor.PoseWithClassification?>
    private lateinit var imageAnalysis: ImageAnalysis

    private lateinit var binding: TaskExerciseBinding

    override lateinit var timer : Timer
    override lateinit var params : JSONObject

    override fun getPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d("test", "don't have permission, getting it")
            requestPermissions(arrayOf<String>(Manifest.permission.CAMERA), 1001 ) //REQUEST_CAMERA
            Log.d("test", "got permission")
//            initSource()// TODO: I added this
        } else {
            Log.d("test", "already have permission")
            initSource()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TaskExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        previewView = findViewById(android.R.id.camera_source_preview)
//        graphicOverlay = findViewById(android.R.id.graphic_overlay)
//        outputTextView = findViewById<TextView>(android.R.id.output_text_view)
//        addFaceButton = findViewById<ExtendedFloatingActionButton>(android.R.id.button_add_face)
        val tv: TextView = findViewById(R.id.output_text_view)
        val graphOv : GraphicOverlay = findViewById(R.id.graphic_overlay)
        val button : ExtendedFloatingActionButton = findViewById(R.id.button_add_face)
        val prevView : PreviewView = findViewById(R.id.camera_source_preview)
        graphicOverlay = graphOv
        outputTextView = tv
        addFaceButton = button
        previewView = prevView
        Globals.poseCount = 0

        cameraProviderFuture = ProcessCameraProvider.getInstance(applicationContext)
        processor = setProcessor()
        val handler = Handler(Looper.getMainLooper())

        init(this, intent)
        var totalReps = 5
        if (params.has("totalReps"))
            totalReps = params.getInt("totalReps")



        fun r() {
            outputTextView.text = "${Globals.poseCount}"
            if (Globals.poseCount >= totalReps) {
                handler.removeCallbacksAndMessages(null)
                outputTextView.text = "Done!"
                handler.postDelayed({
                    destroy()
                    finish()
                }, 1000)
            }
            else
            handler.postDelayed( {
                r()
            }, 500)
        }
        r()



    }

    override fun onDestroy() {
        super.onDestroy()
        destroy()
        if (processor != null) {
            processor.stop()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d("test", "in onReqestPermissionResult" + PackageManager.PERMISSION_GRANTED.toString())
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("test", "permissions granted, going to init source now")
            initSource()
        }
    }

    protected fun setOutputText(text: String?) {
        outputTextView!!.text = "$Globals.poseCount"
    }

    private fun initSource() {
        Log.d("test", "in init source")
        cameraProviderFuture!!.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture!!.get()
                bindPreview(cameraProvider)
            } catch (e: ExecutionException) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            } catch (e: InterruptedException) {
            }
        }, ContextCompat.getMainExecutor(applicationContext))
    }

    fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val lensFacing = lensFacing
        val preview: Preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()
        if (previewView != null) { //TODO: I added this
            preview.setSurfaceProvider(previewView!!.createSurfaceProvider())
        }
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()
        setFaceDetector(lensFacing)
        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)
    }

    /**
     * The face detector provides face bounds whose coordinates, width and height depend on the
     * preview's width and height, which is guaranteed to be available after the preview starts
     * streaming.
     */
    private fun setFaceDetector(lensFacing: Int) {
        previewView?.getPreviewStreamState()?.observe(this, object :
            Observer<PreviewView.StreamState> {
            override fun onChanged(streamState: PreviewView.StreamState) {
                if (streamState !== PreviewView.StreamState.STREAMING) {
                    return
                }
                val preview: View = previewView!!.getChildAt(0)
                var width = preview.width * preview.scaleX
                var height = preview.height * preview.scaleY
                val rotation = preview.display.rotation.toFloat()
                if (rotation == Surface.ROTATION_90.toFloat() || rotation == Surface.ROTATION_270.toFloat()) {
                    val temp = width
                    width = height
                    height = temp
                }
                imageAnalysis.setAnalyzer(
                    executor,
                    createFaceDetector(width.toInt(), height.toInt(), lensFacing)
                )
                previewView!!.getPreviewStreamState().removeObserver(this)
            }
        })
    }

    @OptIn(markerClass = arrayOf(ExperimentalGetImage::class))
    private fun createFaceDetector(
        width: Int,
        height: Int,
        lensFacing: Int
    ): ImageAnalysis.Analyzer {
        graphicOverlay?.setPreviewProperties(width, height, lensFacing)
        return label@ ImageAnalysis.Analyzer { imageProxy ->
            if (imageProxy.getImage() == null) {
                imageProxy.close()
//                return
            }
            val rotationDegrees: Int = imageProxy.getImageInfo().getRotationDegrees()
            // converting from YUV format
            processor.detectInImage(imageProxy, toBitmap(imageProxy.getImage()!!), rotationDegrees)
            // after done, release the ImageProxy object
            imageProxy.close()
        }
    }

    private fun toBitmap(image: Image): Bitmap {
        val planes = image.planes
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        val nv21 = ByteArray(ySize + uSize + vSize)
        //U and V are swapped
        yBuffer[nv21, 0, ySize]
        vBuffer[nv21, ySize, vSize]
        uBuffer[nv21, ySize + vSize, uSize]
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    protected val lensFacing: Int
        protected get() = CameraSelector.LENS_FACING_FRONT

    protected abstract fun setProcessor(): VisionBaseProcessor<PoseDetectorProcessor.PoseWithClassification?>
    fun makeAddFaceVisible() {
        addFaceButton!!.visibility = View.VISIBLE
    }

    fun onAddFaceClicked(view: View?) {}

    companion object {
        private const val REQUEST_CAMERA = 1001
    }
}