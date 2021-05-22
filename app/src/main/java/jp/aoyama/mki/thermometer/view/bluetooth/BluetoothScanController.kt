package jp.aoyama.mki.thermometer.view.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import java.util.*

/**
 * 付近のBluetooth端末を検索し、[BluetoothScanController.devicesLiveData]により配信する。
 * Bluetooth Classic、BLEのどちらでも探索可能で、ペアリングの有無を問わない。
 */
class BluetoothScanController(
    private val context: Context,
    private val timeoutInMillis: Int? = null,
) {
    private val scannerScope = CoroutineScope(Dispatchers.IO)

    private val _devicesLiveData: MutableLiveData<List<BluetoothDeviceData>> =
        MutableLiveData(emptyList())
    val devicesLiveData: LiveData<List<BluetoothDeviceData>> get() = _devicesLiveData

    private var devices: Map<String, BluetoothDeviceData> = emptyMap()
        set(value) {
            // MACアドレス順に並び替え
            val sortedByAddress = value.entries.sortedBy { it.key }.map { it.value }.toMutableList()

            // タイムアウトしたデバイスを削除
            if (timeoutInMillis != null) {
                val now = Calendar.getInstance()
                val timeout =
                    if (timeoutInMillis > SCAN_INTERVAL_IN_MILLI_SEC) timeoutInMillis
                    else SCAN_INTERVAL_IN_MILLI_SEC
                sortedByAddress.removeAll { it.foundAt.timeInMillis < now.timeInMillis - timeout }
            }

            _devicesLiveData.value = sortedByAddress
            field = value
        }

    private val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val mBluetoothScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                            ?: return

                    val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)
                    if (rssi == Short.MIN_VALUE) return

                    val devices = devices.toMutableMap()
                    devices[device.address] =
                        BluetoothDeviceData(device, rssi.toInt(), Calendar.getInstance())
                    this@BluetoothScanController.devices = devices
                }
            }
        }
    }

    /**
     * Bluetooth端末の検索を開始
     * 検索された端末は、[devicesLiveData]に通知されます。
     */
    fun startDiscovery() {
        // Bluetooth端末の検索のスキャン結果を受け取る
        context.registerReceiver(
            mBluetoothScanReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )

        // 検索を開始
        scannerScope.launch {
            while (true) {
                if (mBluetoothAdapter.isDiscovering) mBluetoothAdapter.cancelDiscovery()
                mBluetoothAdapter.startDiscovery()
                delay(SCAN_INTERVAL_IN_MILLI_SEC.toLong())
            }
        }
    }

    /**
     * Bluetooth端末の検索を終了します
     */
    fun cancelDiscovery() {
        if (mBluetoothAdapter.isDiscovering) mBluetoothAdapter.cancelDiscovery()
        context.unregisterReceiver(mBluetoothScanReceiver)
        scannerScope.cancel()
    }

    data class BluetoothDeviceData(
        val device: BluetoothDevice,
        val rssi: Int,
        val foundAt: Calendar,
    )

    companion object {
        // Bluetooth端末の検索インターバル
        // startDiscoveryによる探索期間が12秒であるため
        private const val SCAN_INTERVAL_IN_MILLI_SEC = 12000
    }
}