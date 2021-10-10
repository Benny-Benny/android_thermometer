package jp.aoyama.mki.thermometer.infrastructure.api.bluetooth.devices

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class DeviceEntity(
    val address: String,
    val userId: String
)

data class AddUserDeviceRequest(
    val userId: String,
    val address: String
)

data class DeleteDeviceRequest(
    val address: String
)

interface ApiDeviceService {
    @GET("/devices/")
    fun getDevices(): Call<List<DeviceEntity>>

    @GET("/users/{userId}/devices")
    fun getUserDevices(@Path("userId") userId: String): Call<List<DeviceEntity>>

    @POST("/devices/create")
    fun addUserDevice(@Body request: AddUserDeviceRequest): Call<Unit>

    @POST("/devices/delete")
    fun deleteDevice(@Body request: DeleteDeviceRequest): Call<Unit>
}