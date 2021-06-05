package jp.aoyama.mki.thermometer.domain.models

import java.util.*

data class DeviceData(
    val device: Device,
    val foundAt: Calendar = Calendar.getInstance(),
)