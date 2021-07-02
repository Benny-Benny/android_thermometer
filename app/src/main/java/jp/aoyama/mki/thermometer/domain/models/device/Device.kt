package jp.aoyama.mki.thermometer.domain.models.device

/**
 * Bluetooth端末
 * @param userId 端末に紐づくユーザーのID
 * @param address 端末のMACアドレス
 */
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