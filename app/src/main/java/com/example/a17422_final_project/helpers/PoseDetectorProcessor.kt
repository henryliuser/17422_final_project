package com.example.a17422_final_project.helpers

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.example.a17422_final_project.helpers.PoseClassifierProcessor
import com.google.android.gms.tasks.Task
import com.google.android.odml.image.BitmapMlImageBuilder
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import java.util.concurrent.Executor
import java.util.concurrent.Executors


/** A processor to run pose detector.  */
class PoseDetectorProcessor(
    options: PoseDetectorOptionsBase?,
    private val showInFrameLikelihood: Boolean,
    private val visualizeZ: Boolean,
    private val rescaleZForVisualization: Boolean,
    runClassification: Boolean,
    isStreamMode: Boolean,
    private val context: Context,
    private val graphicOverlay: GraphicOverlay,
    private val previewView: PreviewView
) : VisionBaseProcessor<PoseDetectorProcessor.PoseWithClassification?>() {
    private val detector: PoseDetector
    private val runClassification: Boolean
    private val isStreamMode: Boolean
    private val classificationExecutor: Executor
    private var poseClassifierProcessor: PoseClassifierProcessor? = null

    /** Internal class to hold Pose and classification results.  */
    class PoseWithClassification(val pose: Pose, val classificationResult: List<String>)

    init {
        detector = PoseDetection.getClient(options!!)
        this.runClassification = runClassification
        this.isStreamMode = isStreamMode
        classificationExecutor = Executors.newSingleThreadExecutor()
    }

    override fun stop() {
        detector.close()
    }

    override fun detectInImage(
        imageProxy: ImageProxy?,
        bitmap: Bitmap?,
        rotationDegrees: Int
    ): Task<PoseWithClassification?>? {
        val mlImage = BitmapMlImageBuilder(bitmap!!).setRotation(rotationDegrees).build()
        val rotation = imageProxy!!.imageInfo.rotationDegrees
        // In order to correctly display the face bounds, the orientation of the analyzed
        // image and that of the viewfinder have to match. Which is why the dimensions of
        // the analyzed image are reversed if its rotation information is 90 or 270.
        val reverseDimens = rotation == 90 || rotation == 270
        Log.d(TAG, "rotation: $rotation")
        val width: Int
        val height: Int
        if (reverseDimens) {
            width = imageProxy.height
            height = imageProxy.width
        } else {
            width = imageProxy.width
            height = imageProxy.height
        }
        return detector
            .process(mlImage)
            .continueWith(
                classificationExecutor
            ) { task: Task<Pose> ->
                val pose = task.result
                var classificationResult: List<String?> =
                    ArrayList()
                if (runClassification) {
                    if (poseClassifierProcessor == null) {
                        poseClassifierProcessor = PoseClassifierProcessor(context, isStreamMode)
                    }
                    classificationResult = poseClassifierProcessor!!.getPoseResult(pose)
                }
                PoseWithClassification(pose, classificationResult as List<String>)
            }.addOnSuccessListener { poseWithClassification ->
//                Log.d(
//                    TAG,
//                    "on Success for pose detector"
//                )
                onSuccessPoseClassified(poseWithClassification, width, height)
            }.addOnFailureListener { e ->
                Log.e(
                    TAG,
                    "Pose detection failed!",
                    e
                )
            }
    }

    private fun onSuccessPoseClassified(
        poseWithClassification: PoseWithClassification, width: Int, height: Int
    ) {
        graphicOverlay.clear()
        graphicOverlay.add(
            PoseGraphic(
                graphicOverlay,
                poseWithClassification.pose,
                showInFrameLikelihood,
                visualizeZ,
                rescaleZForVisualization,
                poseWithClassification.classificationResult,
                width,
                height
            )
        )
    }

    companion object {
        private const val TAG = "PoseDetectorProcessor"
    }
}