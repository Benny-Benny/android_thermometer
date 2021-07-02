package jp.aoyama.mki.thermometer.infrastructure.api.bluetooth.state

import android.content.Context
import jp.aoyama.mki.thermometer.domain.models.device.DeviceStateEntity
import jp.aoyama.mki.thermometer.domain.repository.DeviceStateRepository
import jp.aoyama.mki.thermometer.infrastructure.api.ApiRepositoryUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * Raspberry Piに保存された、端末のスキャン結果を参照する。
 */
class ApiDeviceStateRepository(context: Context) : DeviceStateRepository {

    private val service: ApiDeviceStateService by lazy {
        val baseUrl = "${ApiRepositoryUtil(context).baseUrl}/devices/"
        val client = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .client(client)
            .build()

        retrofit.create(ApiDeviceStateService::class.java)
    }

    override suspend fun findAll(): List<DeviceStateEntity> {
        val response = service.getAllDeviceStates().execute().body() ?: emptyList()
        return response.map { it.toDeviceStateEntity() }
    }

    override suspend fun findInRange(start: Calendar, end: Calendar): List<DeviceStateEntity> {
        val request = GetDeviceStatesRequest(start.timeInMillis / 1000, end.timeInMillis / 1000)
        val response = service.getDeviceStatesInDateRange(request).execute().body() ?: emptyList()
        return response.map { it.toDeviceStateEntity() }
    }

    override suspend fun findByAddress(address: String): List<DeviceStateEntity> {
        val response = service.getDeviceStates(address).execute().body() ?: emptyList()
        return response.map { it.toDeviceStateEntity() }
    }

    override suspend fun save(state: DeviceStateEntity) {
        // no op
    }

    override suspend fun delete(id: String) {
        // no op
    }
}