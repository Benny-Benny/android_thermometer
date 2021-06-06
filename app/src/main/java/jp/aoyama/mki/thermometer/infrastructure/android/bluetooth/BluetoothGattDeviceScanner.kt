package jp.aoyama.mki.thermometer.infrastructure.android.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import jp.aoyama.mki.thermometer.domain.models.device.BluetoothScanResult
import jp.aoyama.mki.thermometer.domain.repository.BluetoothDeviceScanner
import kotlinx.coroutines.*
import java.util.*

/**
 *  Bluetooth Classic、BLEのどちらでも検出可能。
 *  また、スリープ状態のデバイスに対しても検出が可能。
 */
class BluetoothGattDeviceScanner(
    private val mContext: Context,
    private val addresses: List<String>,
    private val timeoutInMillis: Int?,
) : BluetoothDeviceScanner {
    private val scannerScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    private var devices: MutableMap<String, BluetoothScanResult> = mutableMapOf()
    private val _devicesLiveData: MutableLiveData<List<BluetoothScanResult>> =
        MutableLiveData(emptyList())
    override val devicesLiveData: LiveData<List<BluetoothScanResult>>
        get() = _devicesLiveData.distinctUntilChanged()

    private val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private val mBluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            if (gatt == null) return
            val address = gatt.device.address ?: return
            devices[address] = BluetoothScanResult(
                address = gatt.device.address,
                name = gatt.device.name,
                foundAt = Calendar.getInstance()
            )

            scannerScope.launch { publishDevices() }

            Log.d(TAG, "onReadRemoteRssi: ${gatt.device.name} ${gatt.device.address} RSSI=$rssi")
        }
    }

    override fun startDiscovery() {
        scannerScope.launch {
            while (true) {
                addresses.forEach { address ->
                    val device = mBluetoothAdapter.getRemoteDevice(address) ?: return@launch
                    val bluetoothGatt = device.connectGatt(
                        mContext,
                        false,
                        mBluetoothGattCallback
                    ) ?: return@launch
                    bluetoothGatt.connect()
                    bluetoothGatt.readRemoteRssi()
                }
                delay(READ_RSSI_INTERVAL_MILLIS.toLong())
                publishDevices()
            }
        }
    }

    override fun cancelDiscovery() {
        scannerScope.cancel()
    }

    private suspend fun publishDevices() {
        // MACアドレス順に並び替え
        val sortedByAddress = devices.values.sortedBy { it.address }.toMutableList()

        // タイムアウトしたデバイスを削除
        if (timeoutInMillis != null) {
            val now = Calendar.getInstance()
            val timeoutMin = READ_RSSI_INTERVAL_MILLIS
            val timeout =
                if (timeoutInMillis > timeoutMin) timeoutInMillis
                else timeoutMin
            sortedByAddress.removeAll { it.foundAt.timeInMillis < now.timeInMillis - timeout }
        }

        withContext(Dispatchers.Main) {
            _devicesLiveData.value = sortedByAddress
        }
    }

    companion object {
        private const val READ_RSSI_INTERVAL_MILLIS = 10 * 1000
        private const val TAG = "BluetoothConnectionScan"
    }
}