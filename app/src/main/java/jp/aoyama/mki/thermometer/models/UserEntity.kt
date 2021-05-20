package jp.aoyama.mki.thermometer.models

import java.util.*

data class UserEntity(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val bluetoothData: BluetoothData?,
    val rssi: Int?,
    val lastFoundAt: Calendar?,
) {
    constructor(user: User, rssi: Int?, lastFoundAt: Calendar?) :
            this(user.id, user.name, user.bluetoothData, rssi, lastFoundAt)
}