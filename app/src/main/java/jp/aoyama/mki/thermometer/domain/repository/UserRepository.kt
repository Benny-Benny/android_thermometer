package jp.aoyama.mki.thermometer.domain.repository

import jp.aoyama.mki.thermometer.domain.models.User

interface UserRepository {

    suspend fun findAll(): List<User>

    suspend fun find(userId: String): User?

    suspend fun save(user: User)

    suspend fun delete(userId: String)
}