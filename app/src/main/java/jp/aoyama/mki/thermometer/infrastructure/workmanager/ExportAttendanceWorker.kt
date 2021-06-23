package jp.aoyama.mki.thermometer.infrastructure.workmanager

import android.content.Context
import android.util.Log
import androidx.work.*
import jp.aoyama.mki.thermometer.domain.service.AttendanceService
import java.util.*
import java.util.concurrent.TimeUnit

class ExportAttendanceWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    private val attendanceService: AttendanceService = AttendanceService(context)

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork: start work")
        val now = Calendar.getInstance()

        val yesterday = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) - 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val tomorrow = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val attendances = attendanceService.getAttendancesOf(yesterday, tomorrow)

        return Result.success()
    }

    companion object {
        private const val TAG = "ExportAttendanceWorker"

        private fun createWorkerRequest(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<ExportAttendanceWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            ).build()
        }

        fun startWork(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniquePeriodicWork(
                "export attendance",
                ExistingPeriodicWorkPolicy.KEEP,
                createWorkerRequest(),
            )
        }
    }
}