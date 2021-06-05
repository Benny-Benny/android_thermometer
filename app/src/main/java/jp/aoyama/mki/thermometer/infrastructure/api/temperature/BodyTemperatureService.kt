package jp.aoyama.mki.thermometer.infrastructure.api.temperature

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class BodyTemperatureData(
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    val temperature: Float,

    @SerializedName("created_at")
    val createdAt: Int
)

data class SaveBodyTemperatureRequest(
    @SerializedName("user_id")
    val userId: String,
    val temperature: Float,
)

interface BodyTemperatureService {
    @GET("/temperature/")
    fun findAll(): Call<List<BodyTemperatureData>>

    @POST("/temperature/create")
    fun save(@Body request: SaveBodyTemperatureRequest): Call<Unit>
}