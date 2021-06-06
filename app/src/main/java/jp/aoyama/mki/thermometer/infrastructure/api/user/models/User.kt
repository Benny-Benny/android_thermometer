package jp.aoyama.mki.thermometer.infrastructure.api.user.models

import jp.aoyama.mki.thermometer.domain.models.user.UserEntity

data class User(
    val id: String,
    val name: String,
    val grade: String?,
    val devices: List<String>,
) {
    fun toUserEntity(): UserEntity {
        return UserEntity(
            id = id,
            name = name,
            grade = grade,
        )
    }
}