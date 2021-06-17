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

        // ユーザーの追加、削除を自動的に行う
        fun List<UserEntity>.updateUsers(users: List<UserEntity>): List<UserEntity> {
            var results: List<UserEntity> = emptyList()
            users.forEach { results = updateUser(it) }

            val deleterUsers = filter { user -> !users.any { it.id == user.id } }
            deleterUsers.forEach { results = deleteUser(it) }

            return results
        }

        fun List<UserEntity>.updateUser(user: UserEntity): List<UserEntity> {
            val users = this.toMutableList()
            // 時刻が指定されていない場合は、前回の状態を維持
            val lastFoundAt = user.lastFoundAt ?: users.find { it.id == user.id }?.lastFoundAt
            users.removeAll { it.id == user.id }
            users.add(user.copy(lastFoundAt = lastFoundAt))
            return users
        }

        fun List<UserEntity>.deleteUser(user: UserEntity): List<UserEntity> {
            val users = this.toMutableList()
            users.removeAll { it.id == user.id }
            return users
        }
    }
}