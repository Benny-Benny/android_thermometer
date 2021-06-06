package jp.aoyama.mki.thermometer.infrastructure.api.bluetooth

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

class ApiBluetoothScanner : BluetoothDeviceScanner {

    private val _deviceLiveData: MutableLiveData<List<BluetoothScanResult>> = MutableLiveData()
    override val devicesLiveData: LiveData<List<BluetoothScanResult>>
        get() = _deviceLiveData

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val service: ApiBluetoothService by lazy {
        val client = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(client)
            .build()

        retrofit.create(ApiBluetoothService::class.java)
    }


    override fun startDiscovery() {
        coroutineScope.launch {
            while (true) {
                val results = scan()
                withContext(Dispatchers.Main) {
                    _deviceLiveData.value = results.map {
                        BluetoothScanResult(
                            address = it.device.address,
                            name = it.device.name,
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
            Log.e(TAG, "request: error while requesting bluetooth scan result", e)
            null
        } ?: return emptyList()

        return response
            .filter { it.found }
            .map {
                val device = Device(name = null, address = it.address, userId = it.userId)
                return@map DeviceData(device)
            }
    }

    override fun cancelDiscovery() {
        coroutineScope.cancel()
    }

    companion object {
        private const val TAG = "BluetoothApiScanner"
        private const val INTERVAL_IN_MILLIS = 10 * 1000
        private const val BASE_URL = "${ApiRepositoryUtil.BASE_URL}/devices/"
    }
}