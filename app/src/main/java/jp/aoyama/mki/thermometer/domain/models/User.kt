package jp.aoyama.mki.thermometer.domain.models

import java.util.*

data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val bluetoothData: BluetoothData?
)