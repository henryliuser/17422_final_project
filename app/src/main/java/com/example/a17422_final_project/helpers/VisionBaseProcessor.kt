package com.example.a17422_final_project.helpers

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task

abstract class VisionBaseProcessor<T> {
    abstract fun detectInImage(
        imageProxy: ImageProxy?,
        bitmap: Bitmap?,
        rotationDegrees: Int
    ): Task<T>?

    abstract fun stop()
}