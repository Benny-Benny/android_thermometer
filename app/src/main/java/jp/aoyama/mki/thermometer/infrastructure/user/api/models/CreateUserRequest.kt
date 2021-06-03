package jp.aoyama.mki.thermometer.infrastructure.user.api.models

data class CreateUserRequest(
    val name: String,
    val devices: List<String>,
    val grade: String?,
)