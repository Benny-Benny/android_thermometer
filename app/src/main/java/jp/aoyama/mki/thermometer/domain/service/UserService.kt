package jp.aoyama.mki.thermometer.domain.service

import jp.aoyama.mki.thermometer.domain.models.BluetoothData
import jp.aoyama.mki.thermometer.domain.models.Grade
import jp.aoyama.mki.thermometer.domain.models.User
import jp.aoyama.mki.thermometer.domain.models.UserEntity
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.api.user.UserApiRepository
import jp.aoyama.mki.thermometer.view.models.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import jp.aoyama.mki.thermometer.view.models.UserEntity as UserViewEntity

class UserService(
    private val userRepository: UserRepository = UserApiRepository()
) {
    suspend fun addUser(user: UserEntity) =
        withContext(Dispatchers.IO) {
            userRepository.save(user)
        }

    suspend fun deleteUser(userId: String) =
        withContext(Dispatchers.IO) {
            userRepository.delete(userId)
        }

    suspend fun getUsers(): UserData =
        withContext(Dispatchers.IO) {
            val users = userRepository.findAll()
            val entities = users.map { UserViewEntity(it.user, null, null) }
            UserData(users = entities)
        }

    suspend fun getUser(userId: String): User? =
        withContext(Dispatchers.IO) {
            userRepository.find(userId)?.user
        }

    suspend fun updateName(userId: String, name: String) =
        withContext(Dispatchers.IO) {
            userRepository.updateName(userId, name)
        }

    suspend fun updateGrade(userId: String, grade: Grade?) =
        withContext(Dispatchers.IO) {
            userRepository.updateGrade(userId, grade)
        }

    suspend fun addBluetoothDevice(userId: String, device: BluetoothData) =
        withContext(Dispatchers.IO) {
            userRepository.addBluetoothDevice(userId, device)
        }

    suspend fun removeBluetoothDevice(userId: String, address: String) =
        withContext(Dispatchers.IO) {
            userRepository.deleteBluetoothDevice(userId, address)
        }
}