package jp.aoyama.mki.thermometer.infrastructure.bluetooth

import jp.aoyama.mki.thermometer.domain.models.BluetoothData
import java.util.*

data class BluetoothDeviceData(
    val device: BluetoothData,
    val foundAt: Calendar = Calendar.getInstance(),
)