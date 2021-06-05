package jp.aoyama.mki.thermometer.infrastructure.bluetooth.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import jp.aoyama.mki.thermometer.domain.models.BluetoothData
import jp.aoyama.mki.thermometer.infrastructure.api.ApiRepositoryUtil
import jp.aoyama.mki.thermometer.infrastructure.bluetooth.BluetoothDeviceData
import jp.aoyama.mki.thermometer.infrastructure.bluetooth.BluetoothDeviceScanner
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BluetoothApiScanner : BluetoothDeviceScanner {

    private val _deviceLiveData: MutableLiveData<List<BluetoothDeviceData>> = MutableLiveData()
    override val devicesLiveData: LiveData<List<BluetoothDeviceData>>
        get() = _deviceLiveData

    private val scope = CoroutineScope(Dispatchers.IO)

    private val service: BluetoothApiService by lazy {
        val client = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(client)
            .build()

        retrofit.create(BluetoothApiService::class.java)
    }


    override fun startDiscovery() {
        scope.launch {
            while (true) {
                val results = scan()
                withContext(Dispatchers.Main) {
                    _deviceLiveData.value = results
                }
                delay(INTERVAL_IN_MILLIS.toLong())
            }
        }
    }

    private fun scan(): List<BluetoothDeviceData> {
        val response = try {
            service.scan().execute().body()
        } catch (e: Exception) {
            Log.e(TAG, "request: error while requesting bluetooth scan result", e)
            null
        } ?: return emptyList()

        return response
            .filter { it.found }
            .map {
                val device = BluetoothData(name = null, address = it.address)
                return@map BluetoothDeviceData(device)
            }
    }

    override fun cancelDiscovery() {
        scope.cancel()
    }

    companion object {
        private const val TAG = "BluetoothApiScanner"
        private const val INTERVAL_IN_MILLIS = 10 * 1000
        private const val BASE_URL = "${ApiRepositoryUtil.BASE_URL}/bluetooth/"
    }
}