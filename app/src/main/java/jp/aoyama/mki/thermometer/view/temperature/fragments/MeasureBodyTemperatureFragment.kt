package jp.aoyama.mki.thermometer.view.temperature.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.databinding.FragmentMeasureBodyTemperatureBinding
import jp.aoyama.mki.thermometer.view.temperature.image.TextRecognizer
import jp.aoyama.mki.thermometer.view.temperature.viewmodels.MeasureBodyTemperatureViewModel
import jp.aoyama.mki.thermometer.view.temperature.viewmodels.TemperatureViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MeasureBodyTemperatureFragment : Fragment(), TextRecognizer.CallbackListener {

    private lateinit var mBinding: FragmentMeasureBodyTemperatureBinding
    private val cameraExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }

    private val mPageViewModel: MeasureBodyTemperatureViewModel by viewModels()
    private val mViewModel: TemperatureViewModel by viewModels()
    private val mArgs by navArgs<MeasureBodyTemperatureFragmentArgs>()
    private val mName: String get() = mArgs.name // 体温を計測する人の名前

    private var mCameraDirection = CameraSelector.LENS_FACING_FRONT

    private val mRequestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) startCamera()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMeasureBodyTemperatureBinding.inflate(inflater, container, false)

        mBinding.apply {
            buttonFlipCamera.setOnClickListener { flipCamera() }
            buttonSave.setOnClickListener { saveTemperature() }
        }

        // Request camera permissions
        val cameraPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (cameraPermission) startCamera()
        else mRequestPermission.launch(Manifest.permission.CAMERA)

        return mBinding.root
    }

    private fun startCamera(cameraSelector: CameraSelector? = null) {
        mBinding.progressCircular.visibility = View.VISIBLE

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder()
            .build()

        // Select back camera
        val selector = cameraSelector ?: CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                val analyzer = TextRecognizer(this@MeasureBodyTemperatureFragment)
                setAnalyzer(cameraExecutor, analyzer)
            }

        cameraProviderFuture.addListener({
            kotlin.runCatching {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this,
                    selector,
                    preview,
                    imageCapture,
                    imageAnalyzer
                )

                preview.setSurfaceProvider(mBinding.viewFinder.surfaceProvider)

                mBinding.progressCircular.visibility = View.GONE
            }.onFailure { e ->
                Log.e(TAG, "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun flipCamera() {
        mCameraDirection =
            if (mCameraDirection == CameraSelector.LENS_FACING_FRONT) CameraSelector.LENS_FACING_BACK
            else CameraSelector.LENS_FACING_FRONT
        val selector = CameraSelector.Builder()
            .requireLensFacing(mCameraDirection)
            .build()
        startCamera(selector)
    }

    private fun saveTemperature() {
        lifecycleScope.launch {
            val valid = mViewModel.saveTemperature(
                requireContext(),
                mName,
                mPageViewModel.getData()
            )
            if (valid) {
                Toast.makeText(context, "保存しました。", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(
                    context,
                    R.string.body_temperature_input_alert,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onScan(texts: List<String>) {
        val showTxt = texts.joinToString(" ")
        mPageViewModel.addData(showTxt)
        val result = mPageViewModel.getData()
        mBinding.textTemperature.text = result?.toString()
        mBinding.buttonSave.isEnabled = result != null
    }

    companion object {
        private const val TAG = "MeasureBodyTemperatureF"
    }
}