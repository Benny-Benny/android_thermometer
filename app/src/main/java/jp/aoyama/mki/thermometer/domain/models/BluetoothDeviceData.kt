package jp.aoyama.mki.thermometer.domain.models

import java.util.*

data class BluetoothDeviceData(
    val device: BluetoothData,
    val foundAt: Calendar = Calendar.getInstance(),
)