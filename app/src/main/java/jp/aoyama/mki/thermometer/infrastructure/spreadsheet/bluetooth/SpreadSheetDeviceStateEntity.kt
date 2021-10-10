package jp.aoyama.mki.thermometer.infrastructure.spreadsheet.bluetooth

import jp.aoyama.mki.thermometer.domain.models.device.DeviceStateEntity
import java.util.*

data class SpreadSheetDeviceStateEntity(
    val id: String,
    val macAddress: String,
    val isFound: Boolean,
    val createdAt: Long
) {
    fun toDeviceState(): DeviceStateEntity {
        return DeviceStateEntity(
            id=id,
            address = macAddress,
            found = isFound,
            createdAt = Calendar.getInstance().apply {
                timeInMillis = createdAt * 1000L
            }
        )
    }

    companion object {
        fun fromCSV(csv: List<String>): SpreadSheetDeviceStateEntity? {
            if (csv.size < 4) return null
            return SpreadSheetDeviceStateEntity(
                id = csv[0],
                macAddress = csv[1],
                isFound = csv[2] == "1",
                createdAt = csv[3].toLongOrNull() ?: 0
            )
        }
    }
}