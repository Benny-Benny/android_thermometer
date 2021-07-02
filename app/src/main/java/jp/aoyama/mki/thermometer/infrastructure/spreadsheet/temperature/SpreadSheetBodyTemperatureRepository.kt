package jp.aoyama.mki.thermometer.infrastructure.spreadsheet.temperature

import android.content.Context
import android.util.Log
import jp.aoyama.mki.thermometer.domain.models.temperature.BodyTemperatureEntity
import jp.aoyama.mki.thermometer.domain.repository.TemperatureRepository
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.SpreadSheetUtil

class SpreadSheetBodyTemperatureRepository(
    context: Context,
    private val mUserRepository: UserRepository,
) : TemperatureRepository {

    private val mSpreadSheet = SpreadSheetUtil(context)

    override suspend fun findAll(): List<BodyTemperatureEntity> {
        return mSpreadSheet.getValues(TEMPERATURE_SHEET_RANGE)
            .mapNotNull { SpreadSheetBodyTemperatureEntity.fromCSV(it) }
            .map { it.toEntity() }
    }

    override suspend fun save(data: BodyTemperatureEntity) {
        val user = mUserRepository.find(data.userId)

        val entity = SpreadSheetBodyTemperatureEntity.fromEntity(
            data,
            user?.name ?: "ユーザーのIDを確認してください"
        )

        mSpreadSheet.appendValues(TEMPERATURE_SHEET_RANGE, listOf(entity.toCSV()))
        Log.d(TAG, "save: saved to spreadsheet ${entity.toCSV()}")
    }

    companion object {
        private const val TAG = "SpreadSheetBodyTemperat"
        private const val TEMPERATURE_SHEET_RANGE = "body_temperature!A:D"
    }

}

