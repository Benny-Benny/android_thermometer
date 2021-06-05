package jp.aoyama.mki.thermometer.domain.models

import java.util.*

data class DeviceStateEntity(
    val address: String,
    val foundAt: Calendar
)