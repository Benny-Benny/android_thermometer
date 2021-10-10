package jp.aoyama.mki.thermometer.infrastructure.spreadsheet.bluetooth

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import jp.aoyama.mki.thermometer.domain.models.device.BluetoothScanResult
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.device.DeviceData
import jp.aoyama.mki.thermometer.domain.models.device.DeviceData.Companion.toDeviceData
import jp.aoyama.mki.thermometer.domain.repository.BluetoothDeviceScanner
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.user.SpreadSheetUserRepository
import kotlinx.coroutines.*

/**
 * SpreadSheetに保存されたスキャン結果をもとに、
 * 擬似的にスキャン結果を作成する。
 */
class SpreadSheetBluetoothScanner(context: Context) : BluetoothDeviceScanner {

    private val _deviceLiveData: MutableLiveData<List<BluetoothScanResult>> = MutableLiveData()
    override val devicesLiveData: LiveData<List<BluetoothScanResult>>
        get() = _deviceLiveData

    private var coroutineScope: CoroutineScope? = null
    private val userRepository = SpreadSheetUserRepository(context)
    private val deviceStateRepository = SpreadSheetDeviceStateRepository(context)

    override fun startDiscovery() {
        coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope?.launch {
            while (true) {
                Log.d(TAG, "startDiscovery: SCAN")
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

    private suspend fun scan(): List<DeviceData> {
        val devices = userRepository.findAll().mapNotNull { it.device }
        val states = devices.mapNotNull { deviceStateRepository.findLatest(it.address) }

        return states
            .filter { it.found }
            .map { Device(address = it.address, userId = it.id) }
            .map { it.toDeviceData() }
    }

    override fun cancelDiscovery() {
        coroutineScope?.cancel()
    }

    companion object {
        private const val TAG = "BluetoothApiScanner"
        private const val INTERVAL_IN_MILLIS = 10 * 1000
    }
}