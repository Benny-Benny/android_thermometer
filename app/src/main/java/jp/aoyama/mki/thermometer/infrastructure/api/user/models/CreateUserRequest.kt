package jp.aoyama.mki.thermometer.infrastructure.api.user.models

data class CreateUserRequest(
    val id: String,
    val name: String,
    val grade: String?,
)