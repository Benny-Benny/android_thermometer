package jp.aoyama.mki.thermometer.infrastructure.local.temperature

import android.content.Context
import jp.aoyama.mki.thermometer.domain.models.temperature.BodyTemperatureEntity
import jp.aoyama.mki.thermometer.domain.repository.TemperatureRepository
import jp.aoyama.mki.thermometer.infrastructure.local.LocalFileBaseRepository

class LocalFileBodyTemperatureRepository(
    context: Context
) : TemperatureRepository {

    private val mBaseRepository = LocalFileBaseRepository(
        context,
        FILE_NAME
    ) { gson, json ->
        gson.fromJson(json, Array<BodyTemperatureEntity>::class.java).toList()
    }

    companion object {
        private const val FILE_NAME = "body_temperature.json"
    }

    override suspend fun findAll(): List<BodyTemperatureEntity> {
        return mBaseRepository.findAll()
    }

    override suspend fun save(data: BodyTemperatureEntity) {
        mBaseRepository.save(data)
    }
}