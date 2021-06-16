package jp.aoyama.mki.thermometer.domain.models.attendance

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

data class AttendanceEntity(
    val userId: String,
    val enterAt: Calendar,
    val leftAt: Calendar?
) {
    @SuppressLint("SimpleDateFormat")
    override fun toString(): String {
        val formatter = SimpleDateFormat()
        val enterAtStr = formatter.format(enterAt.time)
        val leftAtStr = if (leftAt != null) formatter.format(leftAt.time) else null
        return "AttendanceEntity(userId=$userId, enterAt=$enterAtStr, leftAt=$leftAtStr)"
    }

    fun toAttendance(userName: String): Attendance {
        return Attendance(
            userId = userId,
            userName = userName,
            enterAt = enterAt,
            leftAt = leftAt
        )
    }
}