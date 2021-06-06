package jp.aoyama.mki.thermometer.domain.service.data

import jp.aoyama.mki.thermometer.domain.models.Grade
import jp.aoyama.mki.thermometer.domain.models.UserEntity
import jp.aoyama.mki.thermometer.domain.repository.UserRepository

class FakeUserRepository : UserRepository {
    private val baseRepository = FakeBaseRepository<String, UserEntity>()

    override suspend fun findAll(): List<UserEntity> {
        return baseRepository.findALl()
    }

    override suspend fun find(userId: String): UserEntity? {
        return baseRepository.find(userId)
    }

    override suspend fun save(user: UserEntity) {
        return baseRepository.save(user.id, user)
    }

    override suspend fun updateName(userId: String, name: String) {
        val user = baseRepository.find(userId) ?: return
        baseRepository.save(
            userId,
            user.copy(name = name)
        )
    }

    override suspend fun updateGrade(userId: String, grade: Grade?) {
        val user = baseRepository.find(userId) ?: return
        baseRepository.save(
            userId,
            user.copy(grade = grade?.gradeName)
        )
    }

    override suspend fun delete(userId: String) {
        baseRepository.delete(userId)
    }

    fun clear() {
        baseRepository.clear()
    }
}