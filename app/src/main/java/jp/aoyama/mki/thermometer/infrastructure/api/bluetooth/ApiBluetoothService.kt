package jp.aoyama.mki.thermometer.infrastructure.api.bluetooth

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET

data class BluetoothDevice(
    val address: String,
    val found: Boolean,
    @SerializedName("user_id") val userId: String,
)

interface ApiBluetoothService {
    @GET("scan")
    fun scan(): Call<List<BluetoothDevice>>
}