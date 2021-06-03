package jp.aoyama.mki.thermometer.infrastructure.bluetooth.api

import retrofit2.Call
import retrofit2.http.GET

data class BluetoothScanResponse(
    val results: List<BluetoothDevice>
)

data class BluetoothDevice(
    val address: String = "",
    val found: Boolean = false
)

interface BluetoothApiService {
    @GET("/bluetooth/scan")
    fun scan(): Call<BluetoothScanResponse>
}