package jp.aoyama.mki.thermometer.repository

import jp.aoyama.mki.thermometer.models.User

interface UserRepository {

    suspend fun findAll(): List<User>

    suspend fun find(userId: String): User?

    suspend fun save(user: User)

    suspend fun delete(userId: String)
}