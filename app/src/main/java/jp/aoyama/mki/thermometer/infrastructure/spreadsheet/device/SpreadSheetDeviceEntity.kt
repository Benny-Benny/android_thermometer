package jp.aoyama.mki.thermometer.infrastructure.spreadsheet.device

import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.user.SpreadSheetUserEntity

data class SpreadSheetDeviceEntity(
    val userEntity: SpreadSheetUserEntity,
    val macAddress: List<String>
) {
    fun toCSV(): List<String> {
        return userEntity.toCsv() + macAddress
    }

    fun toDevices(): List<Device> {
        return macAddress.map { address ->
            Device(
                userId = userEntity.id,
                name = null,
                address = address
            )
        }
    }

    companion object {
        fun fromCSV(csv: List<String>): SpreadSheetDeviceEntity? {
            // A-C列はユーザーの基本データ
            if (csv.size <= 3) return null
            val user = SpreadSheetUserEntity.fromCSV(csv) ?: return null

            // D列以降をMACアドレスとして取得
            return SpreadSheetDeviceEntity(
                userEntity = user,
                macAddress = csv.subList(3, csv.size).filter { it.isNotBlank() }
            )
        }

        fun fromDevices(
            userEntity: SpreadSheetUserEntity,
            devices: List<Device>
        ): SpreadSheetDeviceEntity {
            return SpreadSheetDeviceEntity(
                userEntity = userEntity,
                macAddress = devices.map { it.address },
            )
        }
    }
}