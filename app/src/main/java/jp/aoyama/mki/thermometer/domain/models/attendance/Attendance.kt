package jp.aoyama.mki.thermometer.domain.models.attendance

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

data class Attendance(
    val userId: String,
    val userName: String,
    val enterAt: Calendar,
    val leftAt: Calendar?
) {
    @SuppressLint("SimpleDateFormat")
    override fun toString(): String {
        val formatter = SimpleDateFormat()
        val enterAtStr = formatter.format(enterAt.time)
        val leftAtStr = if (leftAt != null) formatter.format(leftAt.time) else null
        return "Attendance(userId=$userId, userName=$userName, enterAt=$enterAtStr, leftAt=$leftAtStr)"
    }
}

