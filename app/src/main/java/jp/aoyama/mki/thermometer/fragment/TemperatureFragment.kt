package jp.aoyama.mki.thermometer.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions
import com.google.firebase.ml.vision.text.FirebaseVisionText
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.databinding.TemperatureFragmentBinding
import jp.aoyama.mki.thermometer.viewmodels.TemperatureViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias ODetection = (odt: Array<String?>) -> Unit

private const val TAG = "CameraXBasic"


class TemperatureFragment : Fragment() {

    private lateinit var mBinding: TemperatureFragmentBinding

    private val mViewModel: TemperatureViewModel by viewModels()
    private val mArgs by navArgs<TemperatureFragmentArgs>()
    private val mName: String get() = mArgs.name // 体温を計測する人の名前
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var isPopped = false

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private var frequency = mutableListOf<Float>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = TemperatureFragmentBinding.inflate(inflater, container, false)
        bottomSheetBehavior = BottomSheetBehavior.from(mBinding.bottomSheetLayout)
        frequency.add(0F)
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        // Setup the listener for take photo button
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        return mBinding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {

            }
        }
    }

    //MARK:  ===== カメラ起動 =====
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val frameLayout = FrameLayout(requireContext())
        val context = requireContext()
        val navController = findNavController()
        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                .build()

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    // OCRの結果
                    it.setAnalyzer(cameraExecutor, ImageAnalyze { txtArr ->
                        if(isPopped) return@ImageAnalyze

                        var showTxt = ""
                        frameLayout.removeAllViews()
                        for (txt in txtArr) {
                            txt?.let {
                                showTxt += " $txt"
                            }
                        }
                        if (showTxt != "") {
                        }
                        val numStr = showTxt.replace("[^0-9]".toRegex(), "")
                        if (numStr != "" && numStr.length < 6) {
                            val num = numStr.toInt()
                            if (num in 350..420) {
                                mBinding.bottomSheetText.text = (num.toFloat() / 10F).toString()
                                frequency.add(num.toFloat() / 10F)
                            }else if(num == 31){
                                mBinding.bottomSheetText.text = "37.1"
                                frequency.add(37.1F)
                            }else if(num == 36){
                                mBinding.bottomSheetText.text = "36.1"
                                frequency.add(36.1F)
                            }
                        }

                        if(validate() != 0F){
                            Log.d(TAG, "startCamera: ${validate()}")
                            lifecycleScope.launch {
                                val valid = mViewModel.saveTemperature(context, mName, validate().toString())
                                if (valid) {
                                    Toast.makeText(context, "保存しました。", Toast.LENGTH_LONG).show()
                                    navController.popBackStack()
                                    isPopped = true
                                }else{
                                    Toast.makeText(
                                        context,
                                        R.string.body_temperature_input_alert,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                        Log.d(TAG, "listener fired!: $showTxt")
                    })
                }

            // Select back camera
            val cameraSelector =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )
                preview?.setSurfaceProvider(mBinding.viewFinder.createSurfaceProvider(camera?.cameraInfo))
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun validate() :Float{
        val frequencyMap = frequency.groupingBy { it }.eachCount()
        val max = frequencyMap.maxByOrNull { it.value }
        if( max?.value!! > 5F ){
            return max.key
        }
        return 0F
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireActivity().baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val appMediaDir =
            requireActivity().externalMediaDirs.firstOrNull() ?: requireActivity().filesDir
        // メディアディレクトリを作成
        return File(appMediaDir, "TEMP").apply { mkdirs() }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    // 画像分析 - フレームごとにオブジェクト分類
    class ImageAnalyze(private val listener: ODetection) : ImageAnalysis.Analyzer {
        val options = FirebaseVisionOnDeviceImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.7f)
            .build()
        val labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler(options)

        val detector = FirebaseVision.getInstance()
            .onDeviceTextRecognizer

        private fun degreesToFirebaseRotation(degrees: Int): Int = when (degrees) {
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
        }

        // フレームごとに呼ばれる
        override fun analyze(image: ImageProxy) {
            // Pass image to an ML Kit Vision API
            doObjectClassification(image)
        }

        // 画像分類
        @SuppressLint("UnsafeExperimentalUsageError")
        private fun doObjectClassification(proxy: ImageProxy) {
            val mediaImage = proxy.image ?: return
            val imageRotation = degreesToFirebaseRotation(proxy.imageInfo.rotationDegrees)
            val image = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
            labeler.processImage(image)
                .addOnSuccessListener { labels ->
                    // Task completed successfully
                    for (label in labels) {
                        val text = label.text
                        Log.d(TAG, "text: $text")
                        doTextRecognition(image)

                    }
                    proxy.close()
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    Log.e(TAG, e.toString())
                    proxy.close()
                }
        }

        //文字認識 - 書類に書かれた文字のみ認識する　
        private fun doTextRecognition(image: FirebaseVisionImage) {
            val result = detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    // Task completed successfully
                    parseResultText(firebaseVisionText)
                    Log.d(TAG, "OCR Succeeded!")
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    Log.d(TAG, "OCR Failed...")
                    Log.e(TAG, e.toString())
                }
        }

        // パース - OCRで認識された文字列をParseする
        private fun parseResultText(result: FirebaseVisionText) {
            var resultTxtList: Array<String?> = arrayOf(null)
            for (block in result.textBlocks) {
                val blockText = block.text
                // If you need more data, Uncommentout here
//                val blockConfidence = block.confidence
//                val blockLanguages = block.recognizedLanguages
//                val blockCornerPoints = block.cornerPoints
//                val blockFrame = block.boundingBox
                resultTxtList += blockText
                // If you need more data, Uncommentout here
//                for (line in block.lines) {
//                    val lineText = line.text
//                    val lineConfidence = line.confidence
//                    val lineLanguages = line.recognizedLanguages
//                    val lineCornerPoints = line.cornerPoints
//                    val lineFrame = line.boundingBox
//                    for (element in line.elements) {
//                        val elementText = element.text
//                        val elementConfidence = element.confidence
//                        val elementLanguages = element.recognizedLanguages
//                        val elementCornerPoints = element.cornerPoints
//                        val elementFrame = element.boundingBox
//                    }
//                }
            }
            Log.d("RESULT_TEXT", resultTxtList.toString())
            listener(resultTxtList)
        }
    }

}