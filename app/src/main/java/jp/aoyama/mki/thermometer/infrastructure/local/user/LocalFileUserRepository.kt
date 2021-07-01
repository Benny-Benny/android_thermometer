package jp.aoyama.mki.thermometer.infrastructure.local.user

import android.content.Context
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.domain.models.user.UserEntity
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.local.LocalFileBaseRepository

class LocalFileUserRepository(
    context: Context
) : UserRepository {

    private val mBaseRepository = LocalFileBaseRepository(
        context,
        FILE_NAME
    ) { gson, json ->
        gson.fromJson(json, Array<UserEntity>::class.java).toList()
    }

    companion object {
        private const val FILE_NAME = "users.json"
    }

    override suspend fun findAll(): List<UserEntity> {
        return mBaseRepository.findAll()
    }

    override suspend fun find(userId: String): UserEntity? {
        return mBaseRepository.find { it.id == userId }
    }

    override suspend fun save(user: UserEntity): UserEntity {
        mBaseRepository.save(user)
        return user
    }

    override suspend fun updateName(userId: String, name: String) {
        mBaseRepository.update(
            check = { user -> user.id == userId },
            onUpdate = { it.copy(name = name) }
        )
    }

    override suspend fun updateGrade(userId: String, grade: Grade?) {
        mBaseRepository.update(
            check = { user -> user.id == userId },
            onUpdate = { it.copy(grade = grade?.gradeName) }
        )
    }

    override suspend fun delete(userId: String) {
        mBaseRepository.delete { user ->
            user.id == userId
        }
    }
}