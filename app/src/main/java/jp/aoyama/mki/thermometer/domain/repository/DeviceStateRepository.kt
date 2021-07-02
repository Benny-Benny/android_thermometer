package jp.aoyama.mki.thermometer.domain.repository

import jp.aoyama.mki.thermometer.domain.models.device.DeviceStateEntity
import java.util.*

interface DeviceStateRepository {
    suspend fun findAll(): List<DeviceStateEntity>

    /**
     * 指定した日時の範囲内の DeviceStateEntity　を取得　
     */
    suspend fun findInRange(start: Calendar, end: Calendar): List<DeviceStateEntity>

    suspend fun findByAddress(address: String): List<DeviceStateEntity>

    suspend fun save(state: DeviceStateEntity)

    suspend fun delete(id: String)
}