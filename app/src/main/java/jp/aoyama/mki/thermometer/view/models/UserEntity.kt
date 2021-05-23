package jp.aoyama.mki.thermometer.view.models

import jp.aoyama.mki.thermometer.domain.models.BluetoothData
import jp.aoyama.mki.thermometer.domain.models.User
import java.util.*

data class UserEntity(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val bluetoothDevices: List<BluetoothData>,
    val rssi: Int?,
    val lastFoundAt: Calendar?,
) {
    constructor(user: User, rssi: Int?, lastFoundAt: Calendar?) :
            this(user.id, user.name, user.bluetoothDevices, rssi, lastFoundAt)
}