package jp.aoyama.mki.thermometer.infrastructure.spreadsheet.temperature

import jp.aoyama.mki.thermometer.domain.models.temperature.BodyTemperatureEntity
import java.text.SimpleDateFormat
import java.util.*

data class SpreadSheetBodyTemperatureEntity(
    val userId: String,
    val userName: String,
    val temperature: Float,
    val createdAt: Calendar
) {

    fun toCSV(): List<String> {
        return listOf(
            userId,
            userName,
            temperature.toString(),
            formatter.format(createdAt.time)
        )
    }

    fun toEntity(): BodyTemperatureEntity {
        return BodyTemperatureEntity(
            userId, temperature, createdAt
        )
    }

    companion object {
        private val formatter: SimpleDateFormat by lazy {
            SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.JAPAN
            )
        }

        fun fromCSV(csv: List<String>): SpreadSheetBodyTemperatureEntity? {
            if (csv.size < 4) return null
            val createdAt = formatter.parse(csv[3]) ?: return null

            return SpreadSheetBodyTemperatureEntity(
                userId = csv[0],
                userName = csv[1],
                temperature = csv[2].toFloatOrNull() ?: 0f,
                createdAt = Calendar.getInstance().apply {
                    time = createdAt
                }
            )
        }

        fun fromEntity(
            entity: BodyTemperatureEntity,
            userName: String
        ): SpreadSheetBodyTemperatureEntity {
            return SpreadSheetBodyTemperatureEntity(
                userId = entity.userId,
                userName = userName,
                temperature = entity.temperature,
                createdAt = entity.createdAt
            )
        }
    }
}