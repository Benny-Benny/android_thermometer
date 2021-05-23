package jp.aoyama.mki.thermometer.domain.repository

import jp.aoyama.mki.thermometer.domain.models.TemperatureData

interface TemperatureRepository {
    suspend fun findAll(): List<TemperatureData>

    suspend fun add(data: TemperatureData)
}