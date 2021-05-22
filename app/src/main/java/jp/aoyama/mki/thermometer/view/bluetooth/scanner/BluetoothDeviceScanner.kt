package jp.aoyama.mki.thermometer.view.bluetooth.scanner

import androidx.lifecycle.LiveData

/**
 * 付近のBluetooth端末を検索し、[BluetoothDeviceScanner.devicesLiveData]により配信する。
 */
interface BluetoothDeviceScanner {
    val devicesLiveData: LiveData<List<BluetoothDeviceData>>

    /**
     * Bluetooth端末の検索を開始
     * 検索された端末は、[devicesLiveData]に通知される。
     */
    fun startDiscovery()

    /**
     * Bluetooth端末の検索を終了
     * リソース解放のため、Activity終了時などに必ず呼び出す。
     */
    fun cancelDiscovery()
}

