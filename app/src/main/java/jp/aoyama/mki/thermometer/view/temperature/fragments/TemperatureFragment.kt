package jp.aoyama.mki.thermometer.view.temperature.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.databinding.TemperatureFragmentBinding
import jp.aoyama.mki.thermometer.view.temperature.image.TextRecognizer
import jp.aoyama.mki.thermometer.view.temperature.viewmodels.TemperatureViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TemperatureFragment : Fragment(), TextRecognizer.CallbackListener {

    private lateinit var mBinding: TemperatureFragmentBinding
    private lateinit var cameraExecutor: ExecutorService

    private val mViewModel: TemperatureViewModel by viewModels()
    private val mArgs by navArgs<TemperatureFragmentArgs>()
    private val mName: String get() = mArgs.name // 体温を計測する人の名前

    private var isPopped = false // trueの場合、スキャン結果を無視する
    private var frequency = mutableListOf(0f)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = TemperatureFragmentBinding.inflate(inflater, container, false)
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Request camera permissions
        if (allPermissionsGranted()) startCamera()
        else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        return mBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        isPopped = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_PERMISSIONS -> {
                if (allPermissionsGranted()) startCamera()
            }
        }
    }

    private fun startCamera() {
        mBinding.progressCircular.visibility = View.VISIBLE

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()

            // Select back camera
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    val analyzer = TextRecognizer(this@TemperatureFragment)
                    setAnalyzer(cameraExecutor, analyzer)
                }

            kotlin.runCatching {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                val camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalyzer
                )

                preview.setSurfaceProvider(mBinding.viewFinder.createSurfaceProvider(camera.cameraInfo))
                mBinding.progressCircular.visibility = View.GONE
            }.onFailure { e ->
                Log.e(TAG, "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun saveTemperature() {
        lifecycleScope.launch {
            val valid = mViewModel.saveTemperature(
                requireContext(),
                mName,
                validate().toString()
            )
            if (valid) {
                Toast.makeText(context, "保存しました。", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
                isPopped = true
            } else {
                Toast.makeText(
                    context,
                    R.string.body_temperature_input_alert,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun validate(): Float {
        val frequencyMap = frequency.groupingBy { it }.eachCount()
        val max = frequencyMap.maxByOrNull { it.value } ?: return 0f
        if (max.value < 5F) return 0f
        return max.key
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all { permission ->
        ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onScan(texts: Array<String?>) {
        if (isPopped) return

        val showTxt = texts.joinToString(" ")
        val numStr = showTxt.replace("[^0-9]".toRegex(), "")
        if (numStr.isBlank() || numStr.length > 6) return

        mBinding.textTemperature.text = when (val num = numStr.toInt()) {
            in 350..420 -> {
                frequency.add(num.toFloat() / 10F)
                (num.toFloat() / 10F).toString()
            }
            31 -> {
                frequency.add(37.1F)
                "37.1"
            }
            36 -> {
                frequency.add(36.1F)
                "36.1"
            }
            else -> ""
        }
        if (validate() != 0F) saveTemperature()
    }

    companion object {
        private const val TAG = "TemperatureFragment"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}