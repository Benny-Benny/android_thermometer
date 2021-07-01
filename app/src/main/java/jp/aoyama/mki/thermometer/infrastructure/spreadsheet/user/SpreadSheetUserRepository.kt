package jp.aoyama.mki.thermometer.infrastructure.spreadsheet.user

import android.content.Context
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.domain.models.user.UserEntity
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.SpreadSheetUtil

class SpreadSheetUserRepository(
    context: Context
) : UserRepository {
    companion object {
        private const val USERS_SHEET_RANGE = "users!A:C"
    }

    private val mSpreadSheet = SpreadSheetUtil(context)

    private suspend fun userSheetRangeOf(id: String): String? {
        val row = mSpreadSheet.getColumnOf(USERS_SHEET_RANGE) {
            it[0] == id
        } ?: return null
        return "users!A${row}:C${row}"
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

        val values = listOf(entity.toCsv())
        mSpreadSheet.appendValues(USERS_SHEET_RANGE, values)

        return saveUser
    }

    override suspend fun updateName(userId: String, name: String) {
        val user = find(userId) ?: return
        val range = userSheetRangeOf(userId) ?: return
        val updated = SpreadSheetUserEntity(user.copy(name = name))
        mSpreadSheet.updateValues(range, listOf(updated.toCsv()))
    }

    override suspend fun updateGrade(userId: String, grade: Grade?) {
        val user = find(userId) ?: return
        val range = userSheetRangeOf(userId) ?: return
        val updated = SpreadSheetUserEntity(user.copy(grade = grade?.gradeName))
        mSpreadSheet.updateValues(range, listOf(updated.toCsv()))
    }

    override suspend fun delete(userId: String) {
        val users = getAllEntities().toMutableList()
        users.removeAll { it.id == userId }
        users.sortBy { it.id.toInt() }

        val values = users.map { it.toCsv() }
        mSpreadSheet.clearValues(USERS_SHEET_RANGE)
        mSpreadSheet.appendValues(USERS_SHEET_RANGE, values)
    }
}

