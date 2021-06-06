package jp.aoyama.mki.thermometer.domain.models.temperature

import java.util.*

data class BodyTemperatureEntity(
    val userId: String,
    val temperature: Float,
    val createdAt: Calendar,
) {
    fun toTemperatureData(userName: String): TemperatureData {
        return TemperatureData(
            userId = userId,
            name = userName,
            temperature = temperature,
            createdAt = createdAt
        )
    }
}