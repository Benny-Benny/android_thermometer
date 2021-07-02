package jp.aoyama.mki.thermometer.domain.models.device

data class Device(
    val userId: String,
    val address: String,
) {
    companion object {
        fun create(userId: String, address: String?): Device? {
            if (address == null) return null
            return Device(userId = userId, address = address)
        }
    }
}