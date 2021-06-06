package jp.aoyama.mki.thermometer.domain.models

import java.util.*

data class DeviceStateEntity(
    val id: String = UUID.randomUUID().toString(),
    val address: String,
    val found: Boolean,
    val createdAt: Calendar,
)