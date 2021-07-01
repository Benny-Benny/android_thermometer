package jp.aoyama.mki.thermometer.infrastructure.spreadsheet.device

import jp.aoyama.mki.thermometer.domain.models.device.Device

data class SpreadSheetDeviceEntity(
    val userId: String,
    val macAddress: String
) {

    fun toCSV(): List<String> {
        return listOf(userId, macAddress)
    }

    fun toDevice(): Device {
        return Device(
            userId = userId,
            name = null,
            address = macAddress
        )
    }

    companion object {
        fun fromCSV(csv: List<String>): SpreadSheetDeviceEntity? {
            if (csv.size < 2) return null
            return SpreadSheetDeviceEntity(
                userId = csv[0],
                macAddress = csv[1],
            )
        }

        fun fromDevice(device: Device): SpreadSheetDeviceEntity {
            return SpreadSheetDeviceEntity(
                userId = device.userId,
                macAddress = device.address,
            )
        }
    }
}