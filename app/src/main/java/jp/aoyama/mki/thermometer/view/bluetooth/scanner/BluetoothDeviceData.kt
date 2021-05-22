package jp.aoyama.mki.thermometer.view.bluetooth.scanner

import android.bluetooth.BluetoothDevice
import java.util.*

data class BluetoothDeviceData(
    val device: BluetoothDevice,
    val rssi: Int,
    val foundAt: Calendar,
)