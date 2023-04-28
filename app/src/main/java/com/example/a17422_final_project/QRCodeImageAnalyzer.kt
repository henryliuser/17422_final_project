package com.example.a17422_final_project

import android.graphics.ImageFormat.*
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.multi.qrcode.QRCodeMultiReader

interface QRCodeFoundListener {
    fun onQRCodeFound(qrCode: String?)
    fun qrCodeNotFound()
}

class QRCodeImageAnalyzer(listener: QRCodeFoundListener) : ImageAnalysis.Analyzer {
    private val listener: QRCodeFoundListener

    init {
        this.listener = listener
    }

    override fun analyze(image: ImageProxy) {
        Log.d("test", "in analyzer")
        if (image.format == YUV_420_888 || image.format == YUV_422_888 || image.format == YUV_444_888) {
            val byteBuffer = image.planes[0].buffer
            val imageData = ByteArray(byteBuffer.capacity())
            byteBuffer[imageData]
            val source = PlanarYUVLuminanceSource(
                imageData,
                image.width, image.height,
                0, 0,
                image.width, image.height,
                false
            )
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            try {
                Log.d("test", "found QR code")
                val result = QRCodeMultiReader().decode(binaryBitmap)
                listener.onQRCodeFound(result.text)
            } catch (e: FormatException) {
                listener.qrCodeNotFound()
            } catch (e: ChecksumException) {
                listener.qrCodeNotFound()
            } catch (e: NotFoundException) {
                listener.qrCodeNotFound()
            }
        }
        image.close()
    }
}