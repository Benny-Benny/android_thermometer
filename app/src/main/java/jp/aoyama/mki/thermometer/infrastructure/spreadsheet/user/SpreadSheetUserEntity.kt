package jp.aoyama.mki.thermometer.infrastructure.spreadsheet.user

import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.user.UserEntity

data class SpreadSheetUserEntity(
    val id: String,
    val name: String,
    val grade: String?,
    val deviceMacAddress: String?
) {
    constructor(user: UserEntity) : this(
        id = user.id,
        name = user.name,
        grade = user.grade,
        deviceMacAddress = user.device?.address
    )

    fun toCsv(): List<String> {
        return listOf(
            this.id,
            this.name,
            this.grade ?: "",
            this.deviceMacAddress ?: ""
        )
    }

    fun toEntity(): UserEntity {
        return UserEntity(
            id = id,
            name = name,
            grade = grade,
            device = Device.create(id, deviceMacAddress)
        )
    }

    companion object {
        fun fromCSV(csv: List<String>): SpreadSheetUserEntity? {
            if (csv.size < 2) return null
            return SpreadSheetUserEntity(
                id = csv[0],
                name = csv[1],
                grade = if (csv.size > 2) csv[2] else null,
                deviceMacAddress = if (csv.size > 3) csv[3] else null,
            )
        }
    }
}