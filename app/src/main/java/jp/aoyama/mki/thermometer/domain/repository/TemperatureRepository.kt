package jp.aoyama.mki.thermometer.domain.repository

import jp.aoyama.mki.thermometer.domain.models.TemperatureData

interface TemperatureRepository {
    suspend fun add(data: TemperatureData)
}