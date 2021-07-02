package jp.aoyama.mki.thermometer.infrastructure.spreadsheet.user

import android.content.Context
import android.util.Log
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.domain.models.user.UserEntity
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.SpreadSheetUtil

class SpreadSheetUserRepository(
    context: Context
) : UserRepository {
    companion object {
        // A: id(required), B: name(required), C: grade, D: MAC address
        // 詳細は、SpreadSheetUserEntity#toCSV
        private const val USERS_SHEET_RANGE = "users!A:D"
        private const val TAG = "SpreadSheetUserReposito"
    }

    private val mSpreadSheet = SpreadSheetUtil(context)

    /**
     * userId に紐づく、SpreadSheetの行を取得。
     * 行を取得することで、その範囲だけ更新や削除を行うことができる。
     */
    private suspend fun userSheetRangeOf(userId: String): String? {
        val row = mSpreadSheet.getColumnOf(USERS_SHEET_RANGE) {
            if (it.isEmpty()) return@getColumnOf false
            it[0] == userId
        } ?: return null

        // シート名!行：行 とすることで、行全体を選択することができる。
        return "users!${row}:${row}"
    }

    private suspend fun getAllEntities(): List<SpreadSheetUserEntity> {
        return mSpreadSheet.getValues(USERS_SHEET_RANGE).mapNotNull {
            SpreadSheetUserEntity.fromCSV(it)
        }
    }

    override suspend fun findAll(): List<UserEntity> {
        return getAllEntities().map { it.toEntity() }
    }

    override suspend fun find(userId: String): UserEntity? {
        return findAll().find { it.id == userId }
    }

    override suspend fun save(user: UserEntity): UserEntity {
        delete(user.id)

        val id = getAllEntities().maxOf { it.id.toIntOrNull() ?: 0 } + 1
        val saveUser = user.copy(id = id.toString())
        val entity = SpreadSheetUserEntity(saveUser)

        val values = listOf(entity.toCSV())
        mSpreadSheet.appendValues(USERS_SHEET_RANGE, values)

        return saveUser
    }

    override suspend fun updateName(userId: String, name: String) {
        val user = find(userId) ?: return
        val range = userSheetRangeOf(userId) ?: return
        val updated = SpreadSheetUserEntity(user.copy(name = name))
        mSpreadSheet.updateValues(range, listOf(updated.toCSV()))
    }

    override suspend fun updateGrade(userId: String, grade: Grade?) {
        val user = find(userId) ?: return
        val range = userSheetRangeOf(userId) ?: return
        val updated = SpreadSheetUserEntity(user.copy(grade = grade?.gradeName))
        mSpreadSheet.updateValues(range, listOf(updated.toCSV()))
    }

    override suspend fun updateDevice(userId: String, device: String?) {
        val user = find(userId) ?: return

        val updated = user.copy(device = Device.create(userId, device))
        val entity = SpreadSheetUserEntity(updated)

        val range = userSheetRangeOf(userId) ?: return
        mSpreadSheet.updateValues(range, listOf(entity.toCSV()))

        Log.d(TAG, "updateDevice: update to ${entity.toCSV()}")
    }

    override suspend fun delete(userId: String) {
        val users = getAllEntities().toMutableList()
        users.removeAll { it.id == userId }
        users.sortBy { it.id.toInt() }

        val values = users.map { it.toCSV() }
        mSpreadSheet.clearValues(USERS_SHEET_RANGE)
        mSpreadSheet.appendValues(USERS_SHEET_RANGE, values)
    }
}

