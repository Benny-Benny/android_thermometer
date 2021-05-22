package jp.aoyama.mki.thermometer.view.temperature.image

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions
import com.google.firebase.ml.vision.text.FirebaseVisionText
import jp.aoyama.mki.thermometer.view.temperature.image.TextRecognizer.CallbackListener

/**
 * 画像の中からテキストを抽出する。
 * 抽出されたテキストは、[CallbackListener.onScan]により通知される。
 */
class TextRecognizer(private val mCallbackListener: CallbackListener) : ImageAnalysis.Analyzer {

    interface CallbackListener {
        fun onScan(texts: Array<String?>)
    }

    private val mFirebaseVision by lazy { FirebaseVision.getInstance() }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(proxy: ImageProxy) {
        val mediaImage = proxy.image ?: return
        val imageRotation = degreesToFirebaseRotation(proxy.imageInfo.rotationDegrees)
        val image = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)

        val options = FirebaseVisionOnDeviceImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.7f)
            .build()
        val labeler = mFirebaseVision.getOnDeviceImageLabeler(options)
        labeler.processImage(image)
            .addOnSuccessListener { labels ->
                doTextRecognition(image)
                proxy.close()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "objectClassification: failed", e)
                proxy.close()
            }
    }

    private fun degreesToFirebaseRotation(degrees: Int): Int = when (degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

    //文字認識 - 書類に書かれた文字のみ認識する　
    private fun doTextRecognition(image: FirebaseVisionImage) {
        val detector = mFirebaseVision.onDeviceTextRecognizer
        detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                parseResultText(firebaseVisionText)
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "OCR Failed...", e)
            }
    }

    private fun parseResultText(result: FirebaseVisionText) {
        val resultTextList = result.textBlocks.map { it.text }
        mCallbackListener.onScan(resultTextList.toTypedArray())
    }

    companion object {
        private const val TAG = "ImageAnalyzer"
    }
}