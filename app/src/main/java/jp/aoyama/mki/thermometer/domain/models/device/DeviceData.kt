package jp.aoyama.mki.thermometer.domain.models.device

import java.util.*

data class DeviceData(
    val device: Device,
    val foundAt: Calendar = Calendar.getInstance(),
)