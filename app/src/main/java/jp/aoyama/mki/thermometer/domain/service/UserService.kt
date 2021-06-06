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
import jp.aoyama.mki.thermometer.view.models.UserData
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
        userRepository.save(UserEntity(user))
        user.devices.forEach {
            deviceRepository.save(it)
        }
    }

    suspend fun deleteUser(userId: String) =
        withContext(Dispatchers.IO) {
            userRepository.delete(userId)
        }

    suspend fun getUsers(): UserData =
        withContext(Dispatchers.IO) {
            val users = userRepository.findAll()
            val devices = deviceRepository.findAll()
            val entities = users.map { entity ->
                val userDevices = devices.filter { it.userId == entity.id }
                val user = entity.toUser(userDevices)
                UserViewEntity(user, null, null)
            }
            UserData(users = entities)
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