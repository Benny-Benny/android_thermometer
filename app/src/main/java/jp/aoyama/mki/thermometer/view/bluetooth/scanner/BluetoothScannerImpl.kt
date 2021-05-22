package jp.aoyama.mki.thermometer.view.bluetooth.scanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import kotlinx.coroutines.*
import java.util.*

/**
 * Bluetooth Classic、BLEのどちらでも探索可能で、ペアリングの有無を問わない。
 */
class BluetoothScannerImpl(
    private val context: Context,
    private val timeoutInMillis: Int? = null,
) : BluetoothScanner {
    private val scannerScope = CoroutineScope(Dispatchers.IO)

    private var devices: MutableMap<String, BluetoothDeviceData> = mutableMapOf()
    private val _devicesLiveData: MutableLiveData<List<BluetoothDeviceData>> =
        MutableLiveData(emptyList())
    override val devicesLiveData: LiveData<List<BluetoothDeviceData>> get() = _devicesLiveData.distinctUntilChanged()

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

                    devices[device.address] = BluetoothDeviceData(
                        device = device,
                        rssi = rssi.toInt(),
                        foundAt = Calendar.getInstance()
                    )

                    scannerScope.launch { publishDevices() }
                }
            }
        }
    }

    override fun startDiscovery() {
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
                publishDevices()
            }
        }
    }

    override fun cancelDiscovery() {
        if (mBluetoothAdapter.isDiscovering) mBluetoothAdapter.cancelDiscovery()
        context.unregisterReceiver(mBluetoothScanReceiver)
        scannerScope.cancel()
    }

    /**
     * 検索された端末を[devicesLiveData]に配信
     */
    private suspend fun publishDevices() {
        // MACアドレス順に並び替え
        val sortedByAddress = devices.values.sortedBy { it.device.address }.toMutableList()

        // タイムアウトしたデバイスを削除
        if (timeoutInMillis != null) {
            val now = Calendar.getInstance()
            val timeout =
                if (timeoutInMillis > SCAN_INTERVAL_IN_MILLI_SEC) timeoutInMillis
                else SCAN_INTERVAL_IN_MILLI_SEC
            sortedByAddress.removeAll { it.foundAt.timeInMillis < now.timeInMillis - timeout }
        }

        withContext(Dispatchers.Main) {
            _devicesLiveData.value = sortedByAddress
        }
    }

    companion object {
        // Bluetooth端末の検索インターバル
        // startDiscoveryによる探索期間が12秒であるため
        private const val SCAN_INTERVAL_IN_MILLI_SEC = 12 * 1000
    }
}