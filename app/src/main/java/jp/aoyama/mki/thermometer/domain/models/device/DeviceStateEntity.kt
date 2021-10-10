package jp.aoyama.mki.thermometer.domain.models.device

import java.text.SimpleDateFormat
import java.util.*

/**
 * 端末の検索結果
 * @param address 検索対象となる端末のMACアドレス
 * @param found 発見できたら true
 * @param createdAt 発見日時
 */
data class DeviceStateEntity(
    val id: String = UUID.randomUUID().toString(),
    val address: String,
    val found: Boolean,
    val createdAt: Calendar,
) {
    override fun toString(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPAN)
        val scannedAtStr = formatter.format(createdAt.time)
        return "DeviceStateEntity(address=$address, found=$found, scannedAt=${scannedAtStr})"
    }
}