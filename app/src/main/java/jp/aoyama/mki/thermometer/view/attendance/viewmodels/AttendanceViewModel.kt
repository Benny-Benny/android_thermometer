package jp.aoyama.mki.thermometer.view.attendance.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import jp.aoyama.mki.thermometer.domain.models.attendance.Attendance
import jp.aoyama.mki.thermometer.domain.service.AttendanceService
import java.util.*

class AttendanceViewModel : ViewModel() {

    suspend fun getAttendances(context: Context): List<Attendance> {
        val service = AttendanceService(context)
        return kotlin.runCatching {
            val now = Calendar.getInstance()
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            val tomorrow = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH) + 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            val attendance = service.getAttendances()
            Log.d(TAG, "getAttendances: $attendance")

            attendance
                .flatMap { userAttendance ->
                    val name = userAttendance.userName
                    userAttendance.attendances.map { it.toAttendance(name) }
                }
                .sortedBy { it.enterAt }

        }.fold(
            onSuccess = { it },
            onFailure = { e ->
                Log.e(TAG, "getAttendances: error while getting attendances", e)
                emptyList()
            }
        )
    }

    companion object {
        private const val TAG = "AttendanceViewModel"
    }
}