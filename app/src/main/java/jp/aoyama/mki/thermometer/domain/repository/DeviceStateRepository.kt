package jp.aoyama.mki.thermometer.domain.repository

import jp.aoyama.mki.thermometer.domain.models.DeviceStateEntity

interface DeviceStateRepository {
    suspend fun findAll(): List<DeviceStateEntity>

    suspend fun findByUserId(userId: String): List<DeviceStateEntity>
}