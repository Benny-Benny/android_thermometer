package jp.aoyama.mki.thermometer.domain.models.attendance

import java.text.SimpleDateFormat
import java.util.*

/**
 * 出席データ
 * @param enterAt 入室時間
 * @param leftAt 退出時間(まだ退出していない場合は null になる)
 */
data class Attendance(
    val userId: String,
    val userName: String,
    val enterAt: Calendar,
    val leftAt: Calendar?
) {

    override fun toString(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPAN)
        val enterAtStr = formatter.format(enterAt.time)
        val leftAtStr = if (leftAt != null) formatter.format(leftAt.time) else null
        return "Attendance(userId=$userId, userName=$userName, enterAt=$enterAtStr, leftAt=$leftAtStr)"
    }
}

