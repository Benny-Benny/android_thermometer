package jp.aoyama.mki.thermometer.domain.models.device

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

data class BluetoothScanResult(
    val address: String,
    val name: String?,
    val scannedAt: Calendar
) {
    @SuppressLint("SimpleDateFormat")
    override fun toString(): String {
        val formatter = SimpleDateFormat()
        val scannedAtStr = formatter.format(scannedAt.time)
        return "BluetoothScanResult(address=$address, name=$name, scannedAt=${scannedAtStr})"
    }
}