package jp.aoyama.mki.thermometer.domain.models.device

import java.util.*

data class BluetoothScanResult(
    val address: String,
    val name: String?,
    val foundAt: Calendar
)