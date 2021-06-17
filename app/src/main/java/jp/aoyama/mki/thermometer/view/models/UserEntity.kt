package jp.aoyama.mki.thermometer.view.models

import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.user.User
import java.util.*

data class UserEntity(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val devices: List<Device>,
    val lastFoundAt: Calendar?,
) {
    constructor(user: User, lastFoundAt: Calendar?) :
            this(user.id, user.name, user.devices, lastFoundAt)

    val found: Boolean
        get() {
            val lastFound = this.lastFoundAt ?: return false
            val now = Calendar.getInstance()
            return lastFound.timeInMillis > now.timeInMillis - TIMEOUT_IN_MILLIS
        }

    companion object {
        // 最後に端末を発見してから、1分は表示されるようにする。
        private const val TIMEOUT_IN_MILLIS = 1 * 60 * 1000

        fun List<UserEntity>.updateUser(user: UserEntity): List<UserEntity> {
            val users = this.toMutableList()
            users.removeAll { it.id == user.id }
            users.add(user)
            return users
        }
    }
}