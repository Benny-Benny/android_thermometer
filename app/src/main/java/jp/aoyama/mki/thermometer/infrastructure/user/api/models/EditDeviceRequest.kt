package jp.aoyama.mki.thermometer.infrastructure.user.api.models

data class EditDeviceRequest(
    val devices: List<String>
)