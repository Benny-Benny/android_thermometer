package jp.aoyama.mki.thermometer.domain.repository

import jp.aoyama.mki.thermometer.domain.models.device.Device

interface DeviceRepository {
    suspend fun findAll(): List<Device>

    suspend fun findByUserId(userId: String): List<Device>

    suspend fun save(device: Device)

    suspend fun delete(address: String)

    suspend fun deleteAllByUserId(userId: String)
}