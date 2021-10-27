package jp.aoyama.mki.thermometer.infrastructure.workmanager

import android.content.Context
import android.util.Log
import androidx.work.*
import jp.aoyama.mki.thermometer.infrastructure.calendar.ExportAttendanceToGoogleCalendar
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 出席データをGoogle Calendarに 20 分ごと書き出す
 * インターバルは ExportAttendanceWorker#createWorkerRequest にて指定している。
 */
class ExportAttendanceWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    private val service = ExportAttendanceToGoogleCalendar(context)

    private val today: Calendar
        get() {
            return Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
        }

    private val oneHourAfter: Calendar
        get() {
            return Calendar.getInstance().apply {
                val now = Calendar.getInstance()
                set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
        }

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork: start work")

        service.export(today, oneHourAfter)

        return Result.success()
    }

    companion object {
        private const val TAG = "ExportAttendanceWorker"

        private fun createWorkerRequest(): PeriodicWorkRequest {
            // WorkManagerの最小インターバル15分で実行
            return PeriodicWorkRequestBuilder<ExportAttendanceWorker>(
                repeatInterval = PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                repeatIntervalTimeUnit = TimeUnit.MILLISECONDS
            ).build()
        }

        fun startWork(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniquePeriodicWork(
                "export attendance",
                ExistingPeriodicWorkPolicy.REPLACE,
                createWorkerRequest(),
            )
        }
    }
}