package jp.aoyama.mki.thermometer.view.attendance.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import jp.aoyama.mki.thermometer.domain.models.attendance.Attendance
import jp.aoyama.mki.thermometer.domain.service.AttendanceService

class AttendanceViewModel : ViewModel() {

    suspend fun getAttendances(context: Context): List<Attendance> {
        val service = AttendanceService(context)
        return kotlin.runCatching {
            service.getAttendances()
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