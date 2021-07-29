package jp.aoyama.mki.thermometer.view.temperature.image

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
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
class TextRecognizer(
    private val mCallbackListener: CallbackListener,
    mContext: Context
) : ImageAnalysis.Analyzer {

    interface CallbackListener {
        fun onScan(texts: List<String>)
    }

    private val mTextRecognizer by lazy { TextRecognition.getClient() }
    private var _bitmap: Bitmap? = null
    private val bitmap get() = _bitmap!!
    private val bitmapConverter = YuvToRgbConverter(mContext)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return

        if (_bitmap == null) {
            val bitmap =
                Bitmap.createBitmap(imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888)
            _bitmap = bitmap
        }

        var bitmap = this.bitmap
        bitmapConverter.yuvToRgb(mediaImage, bitmap)
        bitmap = bitmap
            .toGrayScale()
            .lightFilter()
            .contrast(1.5f, 1.5f)

        val image = InputImage.fromBitmap(bitmap, imageProxy.imageInfo.rotationDegrees)

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
                Log.i(TAG, "analyze: error while analyzing image", e)
                imageProxy.close()
            }
    }

    companion object {
        private const val TAG = "ImageAnalyzer"
    }
}