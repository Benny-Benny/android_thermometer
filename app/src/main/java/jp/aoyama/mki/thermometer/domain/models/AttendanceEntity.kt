package jp.aoyama.mki.thermometer.domain.models

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

data class AttendanceEntity(
    val userId: String,
    val enterAt: Calendar,
    val leftAt: Calendar
) {
    @SuppressLint("SimpleDateFormat")
    override fun toString(): String {
        val formatter = SimpleDateFormat()
        val enterAtStr = formatter.format(enterAt.time)
        val leftAtStr = formatter.format(leftAt.time)
        return "AttendanceEntity(userId=$userId, enterAt=$enterAtStr, leftAt=$leftAtStr)"
    }
}