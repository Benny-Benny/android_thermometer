package jp.aoyama.mki.thermometer.domain.models

data class UserEntity(
    val id: String,
    val name: String,
    val bluetoothDevices: List<BluetoothData>,
    val grade: String? = null
) {
    constructor(user: User) : this(
        id = user.id,
        name = user.name,
        bluetoothDevices = user.bluetoothDevices,
        grade = user.grade?.gradeName
    )

    val user
        get(): User {
            val grade = if (grade != null) Grade.fromGradeName(grade) else null

            return User(id, name, bluetoothDevices, grade = grade)
        }
}