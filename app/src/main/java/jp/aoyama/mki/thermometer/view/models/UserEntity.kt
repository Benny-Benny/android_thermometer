package jp.aoyama.mki.thermometer.view.models

import jp.aoyama.mki.thermometer.domain.models.Device
import jp.aoyama.mki.thermometer.domain.models.User
import java.util.*

data class UserEntity(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val devices: List<Device>,
    val rssi: Int?,
    val lastFoundAt: Calendar?,
) {
    constructor(user: User, rssi: Int?, lastFoundAt: Calendar?) :
            this(user.id, user.name, user.devices, rssi, lastFoundAt)
}