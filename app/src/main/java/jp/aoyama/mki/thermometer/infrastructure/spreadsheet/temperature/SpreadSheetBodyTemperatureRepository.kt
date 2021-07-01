package jp.aoyama.mki.thermometer.infrastructure.spreadsheet.temperature

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import jp.aoyama.mki.thermometer.domain.models.temperature.BodyTemperatureEntity
import jp.aoyama.mki.thermometer.domain.repository.TemperatureRepository
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.SpreadSheetUtil
import java.text.SimpleDateFormat
import java.util.*

class SpreadSheetBodyTemperatureRepository(context: Context) : TemperatureRepository {

    private val mSpreadSheet = SpreadSheetUtil(context)

    override suspend fun findAll(): List<BodyTemperatureEntity> {
        return mSpreadSheet.getValues(TEMPERATURE_SHEET_RANGE)
            .mapNotNull { SpreadSheetBodyTemperatureEntity.fromCSV(it) }
            .map { it.toEntity() }
    }

    override suspend fun save(data: BodyTemperatureEntity) {
        val entity = SpreadSheetBodyTemperatureEntity.fromEntity(data)
        mSpreadSheet.appendValues(TEMPERATURE_SHEET_RANGE, listOf(entity.toCSV()))
        Log.d(TAG, "save: saved to spreadsheet ${entity.toCSV()}")
    }

    companion object {
        private const val TAG = "SpreadSheetBodyTemperat"
        private const val TEMPERATURE_SHEET_RANGE = "body_temperature!A:C"
    }

}

data class SpreadSheetBodyTemperatureEntity(
    val userId: String,
    val temperature: Float,
    val createdAt: Calendar
) {

    fun toCSV(): List<String> {
        return listOf(
            userId,
            temperature.toString(),
            formatter.format(createdAt.time)
        )
    }

    fun toEntity(): BodyTemperatureEntity {
        return BodyTemperatureEntity(
            userId, temperature, createdAt
        )
    }

    @SuppressLint("SimpleDateFormat")
    companion object {
        private val formatter: SimpleDateFormat by lazy { SimpleDateFormat("yyyy-MM-dd HH:mm:ss") }

        fun fromCSV(csv: List<String>): SpreadSheetBodyTemperatureEntity? {
            if (csv.size < 3) return null
            val createdAt = formatter.parse(csv[2]) ?: return null

            return SpreadSheetBodyTemperatureEntity(
                userId = csv[0],
                temperature = csv[1].toFloatOrNull() ?: 0f,
                createdAt = Calendar.getInstance().apply {
                    time = createdAt
                }
            )
        }

        fun fromEntity(entity: BodyTemperatureEntity): SpreadSheetBodyTemperatureEntity {
            return SpreadSheetBodyTemperatureEntity(
                userId = entity.userId,
                temperature = entity.temperature,
                createdAt = entity.createdAt
            )
        }
    }
}