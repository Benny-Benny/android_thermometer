package jp.aoyama.mki.thermometer.domain.repository

import jp.aoyama.mki.thermometer.domain.models.device.DeviceStateEntity
import java.util.*

interface DeviceStateRepository {
    suspend fun findAll(): List<DeviceStateEntity>

    suspend fun findInRange(start: Calendar, end: Calendar): List<DeviceStateEntity>

    suspend fun findByAddress(address: String): List<DeviceStateEntity>

    suspend fun save(state: DeviceStateEntity)

    suspend fun delete(id: String)
}