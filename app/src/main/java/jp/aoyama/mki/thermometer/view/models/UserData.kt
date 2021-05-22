package jp.aoyama.mki.thermometer.view.models

import java.util.*

data class UserData private constructor(
    private var _near: MutableList<UserEntity>,
    private var _outs: MutableList<UserEntity>
) {
    val near: List<UserEntity> get() = _near.sortedBy { it.name }
    val outs: List<UserEntity> get() = _outs.sortedBy { it.name }
    val users: List<UserEntity> get() = near + outs

    fun addNearUser(user: UserEntity) {
        _near.removeAll { it.id == user.id }
        _outs.removeAll { it.id == user.id }
        _near.add(user)
        checkExpired()
    }

    /**
     * 最後に検出されたのが、一分以上前のユーザーを削除
     * @return もし、変更がない場合 true を返す
     */
    fun checkExpired(): Boolean {
        val leavedUsers = near.filter {
            val lastFound = it.lastFoundAt ?: return@filter true
            lastFound.timeInMillis < Calendar.getInstance().timeInMillis - 5 * 1000
        }
        _near.removeAll(leavedUsers)
        _outs.addAll(leavedUsers)

        return leavedUsers.isEmpty()
    }

    companion object {
        fun create(near: List<UserEntity>, outs: List<UserEntity>): UserData {
            return UserData(
                _near = near.toMutableList(),
                _outs = outs.toMutableList()
            )
        }

        fun create(users: List<UserEntity>): UserData {
            return UserData(
                _near = users.filter { it.rssi != null }.toMutableList(),
                _outs = users.filter { it.rssi == null }.toMutableList()
            )
        }
    }
}