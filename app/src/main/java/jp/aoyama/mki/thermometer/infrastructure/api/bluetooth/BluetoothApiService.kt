package jp.aoyama.mki.thermometer.infrastructure.api.bluetooth

import retrofit2.Call
import retrofit2.http.GET

data class BluetoothDevice(
    val address: String,
    val found: Boolean,
    val userId: String,
)

interface BluetoothApiService {
    @GET("scan")
    fun scan(): Call<List<BluetoothDevice>>
}