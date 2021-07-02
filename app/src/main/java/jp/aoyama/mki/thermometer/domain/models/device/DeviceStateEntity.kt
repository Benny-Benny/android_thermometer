package jp.aoyama.mki.thermometer.domain.models.device

import java.text.SimpleDateFormat
import java.util.*

data class DeviceStateEntity(
    val id: String = UUID.randomUUID().toString(),
    val address: String,
    val found: Boolean,
    val createdAt: Calendar,
) {
    override fun toString(): String {
        val formatter = SimpleDateFormat()
        val scannedAtStr = formatter.format(createdAt.time)
        return "DeviceStateEntity(address=$address, found=$found, scannedAt=${scannedAtStr})"
    }
}