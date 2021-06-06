package jp.aoyama.mki.thermometer.infrastructure.api.bluetooth

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

data class DeviceStateResponse(
    val id: String,
    val address: String,
    val found: Boolean,
    val createdAt: Int,
)

interface ApiDeviceStateService {
    @GET("states")
    fun getAllDeviceStates(): Call<List<DeviceStateResponse>>

    @GET("states/{address}")
    fun getDeviceStates(@Path("address") address: String): Call<List<DeviceStateResponse>>
}