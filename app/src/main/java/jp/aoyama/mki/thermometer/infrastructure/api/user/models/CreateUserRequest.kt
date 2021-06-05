package jp.aoyama.mki.thermometer.infrastructure.api.user.models

data class CreateUserRequest(
    val name: String,
    val devices: List<String>,
    val grade: String?,
)