package jp.aoyama.mki.thermometer.domain.models

data class BluetoothData(
    val name: String?,
    val address: String,
) {
    companion object {
        fun create(name: String?, address: String?): BluetoothData? {
            if (address == null) return null
            return BluetoothData(name = name, address = address)
        }
    }
}