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

    companion object {
        private const val COMPRESS_TIME_IN_MILLS = 10 * 60 * 1000

        /**
         * 細かく分割された出席データをまとめる。
         * 前の退出時間と次の入室時間の差が10分いないの場合、一つの出席としてまとめる。
         */
        fun List<Attendance>.compress(userId: String): List<Attendance> {
            val userAttendance = filter { it.userId == userId }
            if (userAttendance.isEmpty()) return emptyList()

            val sortedByEnterAt = userAttendance.sortedBy { it.enterAt.timeInMillis }

            val results = mutableListOf(sortedByEnterAt[0])
            sortedByEnterAt.drop(1).forEach { attendance ->
                val prevAttendance = results.last()
                val compressed = compress(prevAttendance, attendance)
                println("$attendance, $compressed")
                if (compressed != null) {
                    results.removeLast()
                    results.add(compressed)
                } else {
                    results.add(attendance)
                }
            }

            return results
        }


        fun compress(prev: Attendance, next: Attendance): Attendance? {
            require(prev.userId == next.userId)
            require(prev.enterAt.timeInMillis <= next.enterAt.timeInMillis)

            val prevLeftAt = prev.leftAt ?: return null
            if (next.enterAt.timeInMillis - prevLeftAt.timeInMillis < COMPRESS_TIME_IN_MILLS) {
                return prev.copy(leftAt = next.leftAt)
            }
            return null
        }
    }
}

