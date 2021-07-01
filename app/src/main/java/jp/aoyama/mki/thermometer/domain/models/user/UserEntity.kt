package jp.aoyama.mki.thermometer.domain.models.user

import jp.aoyama.mki.thermometer.domain.models.device.Device

data class UserEntity(
    val id: String,
    val name: String,
    val grade: String? = null
) {
    constructor(user: User) : this(
        id = user.id,
        name = user.name,
        grade = user.grade?.gradeName
    )

    fun toUser(device: Device?): User {
        val grade = if (grade != null) Grade.fromGradeName(grade) else null

        return User(id, name, device, grade)
    }
}