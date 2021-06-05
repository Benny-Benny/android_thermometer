package jp.aoyama.mki.thermometer.infrastructure.android.bluetooth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import jp.aoyama.mki.thermometer.domain.models.BluetoothScanResult
import jp.aoyama.mki.thermometer.domain.repository.BluetoothDeviceScanner

/**
 *  [BluetoothDiscoveryDeviceScanner], [BluetoothGattDeviceScanner]のどちらの実装も兼ね備えた[BluetoothDeviceScanner].
 *  どちらかでしか検知されない端末があるので、その一時的な対応作。
 */
class BluetoothDeviceScannerImpl(
    context: Context,
    addresses: List<String>,
    timeoutInMillis: Int?
) : BluetoothDeviceScanner {
    private val mDiscoveryScanner =
        BluetoothDiscoveryDeviceScanner(context, timeoutInMillis)
    private val mConnectionScanner =
        BluetoothGattDeviceScanner(context, addresses, timeoutInMillis)

    private val mDeviceData = MediatorLiveData<MutableList<BluetoothScanResult>>().apply {
        addSource(mDiscoveryScanner.devicesLiveData) {
            publishData(it)
        }
        addSource(mConnectionScanner.devicesLiveData) {
            publishData(it)
        }
    }
    override val devicesLiveData: LiveData<List<BluetoothScanResult>>
        get() = mDeviceData.map { it.toList() }

    override fun startDiscovery() {
        mConnectionScanner.startDiscovery()
        mDiscoveryScanner.startDiscovery()
    }

    override fun cancelDiscovery() {
        mDiscoveryScanner.cancelDiscovery()
        mConnectionScanner.cancelDiscovery()
    }

    private fun publishData(devices: List<BluetoothScanResult>) {
        val current = mDeviceData.value
        if (current == null) {
            mDeviceData.value = devices.toMutableList()
            return
        }
        devices.forEach { deviceData ->
            current.removeAll { it.address == deviceData.address }
            current.add(deviceData)
        }
        mDeviceData.value = current.toMutableList()
    }
}