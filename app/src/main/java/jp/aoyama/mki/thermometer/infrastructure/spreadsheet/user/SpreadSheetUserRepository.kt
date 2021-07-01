package jp.aoyama.mki.thermometer.infrastructure.spreadsheet.user

import android.content.Context
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.domain.models.user.UserEntity
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.SpreadSheetUtil

class SpreadSheetUserRepository(
    context: Context
) : UserRepository {
    private val mSpreadSheet = SpreadSheetUtil(context)

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

    override suspend fun save(user: UserEntity) {
        delete(user.id)

        val id = getAllEntities().size.toString()
        val entity = SpreadSheetUserEntity.fromUserEntity(user.copy(id = id))

        val values = listOf(entity.toCsv())
        mSpreadSheet.appendValues(USERS_SHEET_RANGE, values)
    }

    override suspend fun updateName(userId: String, name: String) {
        val user = find(userId) ?: return
        save(user.copy(name = name))
    }

    override suspend fun updateGrade(userId: String, grade: Grade?) {
        val user = find(userId) ?: return
        save(user.copy(grade = grade?.gradeName))
    }

    override suspend fun delete(userId: String) {
        val users = getAllEntities().toMutableList()
        users.removeAll { it.id == userId }
        users.sortBy { it.id.toInt() }

        val values = users.map { it.toCsv() }
        mSpreadSheet.clearValues(USERS_SHEET_RANGE)
        mSpreadSheet.appendValues(USERS_SHEET_RANGE, values)
    }

    companion object {
        private const val USERS_SHEET_RANGE = "users!A:C"
    }
}

