package jp.aoyama.mki.thermometer.domain.models

import java.util.*

data class TemperatureData(
    val temperature: Float,
    val userId: String,
    val name: String,
    val createdAt: Calendar = Calendar.getInstance()
)