package jp.aoyama.mki.thermometer.infrastructure.api.bluetooth

import jp.aoyama.mki.thermometer.domain.models.device.DeviceStateEntity
import jp.aoyama.mki.thermometer.domain.repository.DeviceStateRepository
import jp.aoyama.mki.thermometer.infrastructure.api.ApiRepositoryUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class ApiDeviceStateRepository : DeviceStateRepository {

    private val service: ApiDeviceStateService by lazy {
        val client = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(client)
            .build()

        retrofit.create(ApiDeviceStateService::class.java)
    }

    override suspend fun findAll(): List<DeviceStateEntity> {
        val response = service.getAllDeviceStates().execute().body() ?: emptyList()
        return response.map {
            DeviceStateEntity(
                id = it.id,
                address = it.address,
                found = it.found,
                createdAt = getCalendar(it.createdAt)
            )
        }
    }

    override suspend fun findByAddress(address: String): List<DeviceStateEntity> {
        val response = service.getDeviceStates(address).execute().body() ?: emptyList()
        return response.map {

            DeviceStateEntity(
                id = it.id,
                address = it.address,
                found = it.found,
                createdAt = getCalendar(it.createdAt)
            )
        }
    }

    override suspend fun save(state: DeviceStateEntity) {
        // no op
    }

    override suspend fun delete(id: String) {
        // no op
    }

    private fun getCalendar(time: Int): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = time * 1000L
        }
    }

    companion object {
        private const val BASE_URL = "${ApiRepositoryUtil.BASE_URL}/devices/"
    }
}