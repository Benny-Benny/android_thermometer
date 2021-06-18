package jp.aoyama.mki.thermometer.infrastructure.repositories

import android.util.Log
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.repository.DeviceRepository

/**
 * ローカルとリモートの双方でデータを保存する
 *
 * Raspberry Pi上では、端末スキャン時に端末の情報を必要とするが、
 * データはなるべくローカルで完結させたいため、このような措置をとっている。
 */
class DeviceRepositoryImpl(
    private val localRepository: DeviceRepository,
    private val remoteRepository: DeviceRepository,
) : DeviceRepository {

    override suspend fun findAll(): List<Device> {
        kotlin.runCatching {
            checkConsistency()
        }.onFailure { e ->
            Log.e(TAG, "findAll: error while checking consistency", e)
        }
        return localRepository.findAll()
    }

    override suspend fun findByUserId(userId: String): List<Device> {
        return localRepository.findByUserId(userId)
    }

    override suspend fun save(device: Device) {
        localRepository.save(device)
        kotlin.runCatching {
            remoteRepository.save(device)
        }.onFailure { e ->
            Log.e(TAG, "save: error while saving on remote repository", e)
        }
    }

    override suspend fun delete(address: String) {
        localRepository.delete(address)
        kotlin.runCatching {
            remoteRepository.delete(address)
        }.onFailure { e ->
            Log.e(TAG, "delete: error while saving on remote repository", e)
        }
    }

    /**
     * 整合性の確認
     * リモートに無いデータは追加し、ローカルから削除されたデータはリモートからも削除する。
     */
    private suspend fun checkConsistency() {
        val localData = localRepository.findAll()
        val remoteData = remoteRepository.findAll()

        val addedData = localData.filter { device ->
            !remoteData.any { it.address == device.address }
        }

        val removedData = remoteData.filter { device ->
            !localData.any { it.address == device.address }
        }

        addedData.forEach { remoteRepository.save(it) }
        removedData.forEach { remoteRepository.delete(it.address) }
    }

    companion object {
        private const val TAG = "DeviceRepositoryImpl"
    }
}