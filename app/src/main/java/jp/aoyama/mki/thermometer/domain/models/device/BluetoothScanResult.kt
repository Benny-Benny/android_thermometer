package jp.aoyama.mki.thermometer.domain.models.device

import java.text.SimpleDateFormat
import java.util.*

data class BluetoothScanResult(
    val address: String,
    val name: String?,
    val scannedAt: Calendar
) {

    override fun toString(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPAN)
        val scannedAtStr = formatter.format(scannedAt.time)
        return "BluetoothScanResult(address=$address, name=$name, scannedAt=${scannedAtStr})"
    }
}