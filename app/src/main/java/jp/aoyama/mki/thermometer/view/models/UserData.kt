package jp.aoyama.mki.thermometer.view.models

import java.util.*

data class UserData constructor(
    val users: List<UserEntity>
) {
    val near: List<UserEntity> get() = users.filter { !it.isExpired() }.sortedBy { it.name }
    val outs: List<UserEntity> get() = users.filter { it.isExpired() }.sortedBy { it.name }

    fun addNearUser(user: UserEntity): UserData {
        val users = this.users.toMutableList()
        users.removeAll { it.id == user.id }
        users.add(user)
        return copy(users = users)
    }

    private fun UserEntity.isExpired(): Boolean {
        val lastFound = this.lastFoundAt ?: return true
        val now = Calendar.getInstance()
        return lastFound.timeInMillis < now.timeInMillis - TIMEOUT_IN_MILLIS
    }

    companion object {
        private const val TIMEOUT_IN_MILLIS = 5 * 1000
    }
}