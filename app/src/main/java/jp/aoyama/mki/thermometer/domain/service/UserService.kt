package jp.aoyama.mki.thermometer.domain.service

import android.content.Context
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.temperature.BodyTemperatureEntity
import jp.aoyama.mki.thermometer.domain.models.temperature.TemperatureData
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.domain.models.user.User
import jp.aoyama.mki.thermometer.domain.models.user.UserEntity
import jp.aoyama.mki.thermometer.domain.repository.DeviceRepository
import jp.aoyama.mki.thermometer.domain.repository.TemperatureRepository
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.repositories.RepositoryContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import jp.aoyama.mki.thermometer.view.models.UserEntity as UserViewEntity

class UserService(
    private val userRepository: UserRepository,
    private val deviceRepository: DeviceRepository,
    private val temperatureRepository: TemperatureRepository,
) {

    constructor(context: Context) : this(
        userRepository = RepositoryContainer(context).userRepository,
        deviceRepository = RepositoryContainer(context).deviceRepository,
        temperatureRepository = RepositoryContainer(context).temperatureRepository
    )

    suspend fun createUser(user: User) = withContext(Dispatchers.IO) {
        val savedUser = userRepository.save(UserEntity(user))
        val device = user.device ?: return@withContext
        deviceRepository.save(device.copy(userId = savedUser.id))
    }

    suspend fun deleteUser(userId: String) =
        withContext(Dispatchers.IO) {
            userRepository.delete(userId)
        }

    suspend fun getUsers(): List<UserViewEntity> =
        withContext(Dispatchers.IO) {
            val users = userRepository.findAll()
            val devices = deviceRepository.findAll()
            return@withContext users.map { entity ->
                val device = devices.find { it.userId == entity.id }
                val user = entity.toUser(device)
                UserViewEntity(user, null)
            }
        }

    suspend fun getUser(userId: String): User? =
        withContext(Dispatchers.IO) {
            val entity = userRepository.find(userId) ?: return@withContext null
            val device = deviceRepository.findByUserId(userId)
            entity.toUser(device)
        }

    suspend fun updateName(userId: String, name: String) =
        withContext(Dispatchers.IO) {
            userRepository.updateName(userId, name)
        }

    suspend fun updateGrade(userId: String, grade: Grade?) =
        withContext(Dispatchers.IO) {
            userRepository.updateGrade(userId, grade)
        }

    suspend fun addBluetoothDevice(device: Device) =
        withContext(Dispatchers.IO) {
            deviceRepository.save(device)
        }

    suspend fun removeBluetoothDevice(address: String) =
        withContext(Dispatchers.IO) {
            deviceRepository.delete(address)
        }

    /**
     * 入力された体温を保存
     * @param userId 体温を記録する人のID
     * @param temperature 体温
     * @return 入力値が適切であれば true を返す
     */
    suspend fun saveTemperature(userId: String, temperature: Float?): Boolean {
        if (temperature == null) return false
        if (temperature > 45f || temperature < 35f) return false

        val data = BodyTemperatureEntity(
            userId = userId,
            temperature = temperature,
            createdAt = Calendar.getInstance()
        )
        withContext(Dispatchers.IO) {
            temperatureRepository.save(data)
        }

        return true
    }

    suspend fun getTemperatureData(): List<TemperatureData> = withContext(Dispatchers.IO) {
        val users = userRepository.findAll()
        temperatureRepository.findAll()
            .mapNotNull { entity ->
                val user = users.find { it.id == entity.userId } ?: return@mapNotNull null
                entity.toTemperatureData(user.name)
            }
            .sortedBy { it.createdAt.timeInMillis }
            .reversed()
    }
}