package jp.aoyama.mki.thermometer.infrastructure.api.user.models

data class EditDeviceRequest(
    val devices: List<String>
)