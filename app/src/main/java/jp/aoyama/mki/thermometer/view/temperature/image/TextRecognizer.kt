package jp.aoyama.mki.thermometer.view.temperature.image

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import jp.aoyama.mki.thermometer.view.temperature.image.TextRecognizer.CallbackListener

/**
 * 画像の中からテキストを抽出する。
 * 抽出されたテキストは、[CallbackListener.onScan]により通知される。
 */
class TextRecognizer(private val mCallbackListener: CallbackListener) : ImageAnalysis.Analyzer {

    interface CallbackListener {
        fun onScan(texts: List<String>)
    }

    private val mTextRecognizer by lazy { TextRecognition.getClient() }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        mTextRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                val texts = visionText.textBlocks
                    .flatMap<Text.TextBlock, String> { block ->
                        block.lines.map { line -> line.text }
                    }
                mCallbackListener.onScan(texts)
                imageProxy.close()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "analyze: error while analyzing image", e)
                imageProxy.close()
            }
    }

    companion object {
        private const val TAG = "ImageAnalyzer"
    }
}