package jp.aoyama.mki.thermometer.infrastructure.api.temperature

import android.content.Context
import jp.aoyama.mki.thermometer.domain.models.temperature.BodyTemperatureEntity
import jp.aoyama.mki.thermometer.domain.repository.TemperatureRepository
import jp.aoyama.mki.thermometer.infrastructure.api.ApiRepositoryUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class ApiTemperatureRepository(context: Context) : TemperatureRepository {
    private val service: BodyTemperatureService by lazy {
        val baseUrl = ApiRepositoryUtil(context).baseUrl
        val client = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .client(client)
            .build()

        retrofit.create(BodyTemperatureService::class.java)
    }

    override suspend fun findAll(): List<BodyTemperatureEntity> {
        val response = service.findAll().execute()
        return response.body()?.map {
            BodyTemperatureEntity(
                temperature = it.temperature,
                userId = it.userId,
                createdAt = Calendar.getInstance().apply {
                    timeInMillis = it.createdAt.toLong() * 1000
                }
            )
        } ?: emptyList()
    }

    override suspend fun save(data: BodyTemperatureEntity) {
        val request = SaveBodyTemperatureRequest(
            userId = data.userId,
            temperature = data.temperature
        )
        service.save(request).execute()
    }
}