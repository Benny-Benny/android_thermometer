package jp.aoyama.mki.thermometer.infrastructure.api.bluetooth.scanner

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import jp.aoyama.mki.thermometer.domain.models.device.BluetoothScanResult
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.device.DeviceData
import jp.aoyama.mki.thermometer.domain.repository.BluetoothDeviceScanner
import jp.aoyama.mki.thermometer.infrastructure.api.ApiRepositoryUtil
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Raspberry Piに保存されたスキャン結果をもとに、
 * 擬似的にスキャン結果を作成する。
 */
class ApiBluetoothScanner(context: Context) : BluetoothDeviceScanner {

    private val _deviceLiveData: MutableLiveData<List<BluetoothScanResult>> = MutableLiveData()
    override val devicesLiveData: LiveData<List<BluetoothScanResult>>
        get() = _deviceLiveData

    private var coroutineScope: CoroutineScope? = null

    private val service: ApiBluetoothService by lazy {
        val baseUrl = "${ApiRepositoryUtil(context).baseUrl}/devices/"
        val client = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .client(client)
            .build()

        retrofit.create(ApiBluetoothService::class.java)
    }


    override fun startDiscovery() {
        coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope?.launch {
            while (true) {
                val results = kotlin
                    .runCatching { scan() }
                    .fold(
                        onSuccess = { it },
                        onFailure = { e ->
                            Log.i(TAG, "startDiscovery: error while scanning", e)
                            emptyList()
                        }
                    )

                withContext(Dispatchers.Main) {
                    _deviceLiveData.value = results.map {
                        BluetoothScanResult(
                            name = null,
                            address = it.device.address,
                            scannedAt = it.foundAt
                        )
                    }
                }
                delay(INTERVAL_IN_MILLIS.toLong())
            }
        }
    }

    private fun scan(): List<DeviceData> {
        val response = try {
            service.scan().execute().body()
        } catch (e: Exception) {
            Log.i(TAG, "request: error while requesting bluetooth scan result", e)
            null
        } ?: return emptyList()

        return response
            .filter { it.found }
            .map {
                val device = Device(address = it.address, userId = it.userId)
                return@map DeviceData(device)
            }
    }

    override fun cancelDiscovery() {
        coroutineScope?.cancel()
    }

    companion object {
        private const val TAG = "BluetoothApiScanner"
        private const val INTERVAL_IN_MILLIS = 10 * 1000
    }
}