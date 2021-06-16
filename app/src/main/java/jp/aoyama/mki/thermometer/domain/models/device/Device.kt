package jp.aoyama.mki.thermometer.domain.models.device

data class Device(
    val name: String? = null,
    val userId: String,
    val address: String,
) {
    companion object {
        fun create(name: String?, userId: String, address: String?): Device? {
            if (address == null) return null
            return Device(name = name, userId = userId, address = address)
        }
    }
}