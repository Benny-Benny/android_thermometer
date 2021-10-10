package jp.aoyama.mki.thermometer.domain.models.device

import java.util.*

/**
 * 端末の検索情報
 * 端末の情報に加え、端末の発見時間を記録する。
 */
data class DeviceData(
    val device: Device,
    val foundAt: Calendar = Calendar.getInstance(),
) {
    companion object {
        fun Device.toDeviceData(): DeviceData {
            return DeviceData(this)
        }
    }
}