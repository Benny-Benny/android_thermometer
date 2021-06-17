package jp.aoyama.mki.thermometer.infrastructure.api.bluetooth

import jp.aoyama.mki.thermometer.domain.models.device.DeviceStateEntity
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.*

data class DeviceStateResponse(
    val id: String,
    val address: String,
    val found: Boolean,
    val createdAt: Int,
) {
    fun toDeviceStateEntity(): DeviceStateEntity {
        val createdAt = Calendar.getInstance().apply {
            timeInMillis = createdAt * 1000L
        }
        return DeviceStateEntity(
            id = id,
            address = address,
            found = found,
            createdAt = createdAt
        )
    }
}

data class GetDeviceStatesRequest(
    val start: Long,
    val end: Long
)

interface ApiDeviceStateService {
    @GET("states")
    fun getAllDeviceStates(): Call<List<DeviceStateResponse>>

    @POST("states")
    fun getDeviceStatesInDateRange(@Body request: GetDeviceStatesRequest): Call<List<DeviceStateResponse>>

    @GET("states/{address}")
    fun getDeviceStates(@Path("address") address: String): Call<List<DeviceStateResponse>>
}