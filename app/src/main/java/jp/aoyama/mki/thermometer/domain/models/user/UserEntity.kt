package jp.aoyama.mki.thermometer.domain.models.user

import jp.aoyama.mki.thermometer.domain.models.device.Device

data class UserEntity(
    val id: String,
    val name: String,
    val grade: String? = null,
    val device: Device?
) {
    constructor(user: User) : this(
        id = user.id,
        name = user.name,
        grade = user.grade?.gradeName,
        device = user.device
    )

    fun toUser(): User {
        val grade = if (grade != null) Grade.fromGradeName(grade) else null
        return User(
            id = id,
            name = name,
            device = device,
            grade = grade
        )
    }
}