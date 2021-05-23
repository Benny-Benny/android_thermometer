package jp.aoyama.mki.thermometer.domain.repository

import jp.aoyama.mki.thermometer.domain.models.BluetoothData
import jp.aoyama.mki.thermometer.domain.models.Grade
import jp.aoyama.mki.thermometer.domain.models.UserEntity

interface UserRepository {

    suspend fun findAll(): List<UserEntity>

    suspend fun find(userId: String): UserEntity?

    suspend fun save(user: UserEntity)

    suspend fun updateName(userId: String, name: String)

    suspend fun updateGrade(userId: String, grade: Grade?)

    suspend fun addBluetoothDevice(userId: String, bluetooth: BluetoothData)

    suspend fun deleteBluetoothDevice(userId: String, address: String)

    suspend fun delete(userId: String)
}