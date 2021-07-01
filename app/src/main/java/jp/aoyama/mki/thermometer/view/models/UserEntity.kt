package jp.aoyama.mki.thermometer.view.models

import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.domain.models.user.User
import java.util.*

data class UserEntity(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val grade: Grade?,
    val device: Device?,
    val lastFoundAt: Calendar?,
) {
    constructor(user: User, lastFoundAt: Calendar?) :
            this(user.id, user.name, user.grade, user.device, lastFoundAt)

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
            // 時刻が指定されていない場合は、前回の状態を維持
            val lastFoundAt = user.lastFoundAt ?: users.find { it.id == user.id }?.lastFoundAt
            users.removeAll { it.id == user.id }
            users.add(user.copy(lastFoundAt = lastFoundAt))
            return users
        }
    }
}