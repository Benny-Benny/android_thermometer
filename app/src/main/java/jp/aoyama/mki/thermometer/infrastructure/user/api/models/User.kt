package jp.aoyama.mki.thermometer.infrastructure.user.api.models

import jp.aoyama.mki.thermometer.domain.models.BluetoothData
import jp.aoyama.mki.thermometer.domain.models.UserEntity

data class User(
    val id: String,
    val name: String,
    val grade: String?,
    val devices: List<String>,
) {
    fun toUserEntity(): UserEntity {
        return UserEntity(
            id = id,
            name = name,
            grade = grade,
            bluetoothDevices = devices.map {
                BluetoothData(name = name, address = it)
            }
        )
    }
}