package jp.aoyama.mki.thermometer.domain.models.attendance

import jp.aoyama.mki.thermometer.domain.models.attendance.Attendance.Companion.compress

/**
 * ユーザーごとの出席データ
 * @param attendances ユーザーのすべての出席データ
 */
data class UserAttendance(
    val userId: String,
    val userName: String,
    val attendances: List<AttendanceEntity>
) {
    fun toAttendanceList(): List<Attendance> {
        return attendances
            .map { it.toAttendance(userName) }
            .compress(userId)
    }
}