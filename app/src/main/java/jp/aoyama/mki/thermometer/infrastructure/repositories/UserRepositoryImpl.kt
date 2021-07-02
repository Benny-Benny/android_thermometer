package jp.aoyama.mki.thermometer.infrastructure.repositories

import android.util.Log
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.domain.models.user.UserEntity
import jp.aoyama.mki.thermometer.domain.repository.DeviceRepository
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Raspberry Piで実行される、端末スキャンで使用する
 * 端末一覧データと、ユーザーデータに含まれる端末情報を一致させる。
 */
class UserRepositoryImpl(
    private val userRepository: UserRepository,
    private val deviceRepository: DeviceRepository
) : UserRepository {

    private val mScope = CoroutineScope(Dispatchers.IO)

    override suspend fun findAll(): List<UserEntity> {
        val users = userRepository.findAll()
        mScope.launch {
            kotlin.runCatching {
                checkConsistency(users)
            }.onFailure { e ->
                Log.d(TAG, "findAll: error while checking consistency", e)
            }
        }
        return users
    }

    override suspend fun find(userId: String): UserEntity? {
        return userRepository.find(userId)
    }

    override suspend fun save(user: UserEntity): UserEntity {
        val savedUser = userRepository.save(user)

        kotlin.runCatching {
            val device = savedUser.device
            if (device != null) deviceRepository.save(device)
        }.onFailure { e ->
            Log.d(TAG, "save: error while save device", e)
        }

        return savedUser
    }

    override suspend fun updateName(userId: String, name: String) {
        userRepository.updateName(userId, name)
    }

    override suspend fun updateGrade(userId: String, grade: Grade?) {
        userRepository.updateGrade(userId, grade)
    }

    override suspend fun updateDevice(userId: String, device: String?) {
        Log.d(TAG, "updateDevice: update device")
        userRepository.updateDevice(userId, device)

        kotlin.runCatching {
            if (device != null) deviceRepository.save(Device(userId, device))
            else deleteUserDevice(userId)
        }.onFailure { e ->
            Log.d(TAG, "updateDevice: error while updating device", e)
        }
    }

    private suspend fun deleteUserDevice(userId: String) {
        val device = userRepository.find(userId)?.device ?: return
        deviceRepository.delete(device.address)
    }

    override suspend fun delete(userId: String) {
        userRepository.delete(userId)
    }


    /**
     * 整合性の確認
     * リモートに無いデータは追加し、ローカルから削除されたデータはリモートからも削除する。
     */
    private suspend fun checkConsistency(users: List<UserEntity>? = null) {
        val u = users ?: userRepository.findAll()
        val userDevices = u.mapNotNull { it.device }
        val savedDevices = deviceRepository.findAll()

        val addedData = userDevices.filter { device ->
            !savedDevices.any { it.address == device.address }
        }

        val removedData = savedDevices.filter { device ->
            !userDevices.any { it.address == device.address }
        }

        addedData.forEach { deviceRepository.save(it) }
        removedData.forEach { deviceRepository.delete(it.address) }
    }

    companion object {
        private const val TAG = "UserRepositoryImpl"
    }
}