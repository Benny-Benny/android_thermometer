package jp.aoyama.mki.thermometer.infrastructure.workmanager

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.android.gms.common.Scopes
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.sheets.v4.SheetsScopes
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.domain.models.attendance.UserAttendance
import jp.aoyama.mki.thermometer.domain.service.AttendanceService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit
import com.google.api.services.calendar.Calendar as GoogleCalendar

/**
 * 出席データをGoogle Calendarに 20 分ごと書き出す
 * インターバルは ExportAttendanceWorker#createWorkerRequest にて指定している。
 */
class ExportAttendanceWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    private val attendanceService: AttendanceService = AttendanceService(context)
    private val calendarId get() = context.getString(R.string.calendar_id)

    private val yesterday: Calendar
        get() {
            val now = Calendar.getInstance()
            return Calendar.getInstance().apply {
                set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) - 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
        }

    private val tomorrow: Calendar
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

        val attendances = attendanceService.getAttendancesOf(yesterday, tomorrow)

        val transport = NetHttpTransport.Builder().build()
        val jsonFactory = JacksonFactory.getDefaultInstance()

        val credentialFileName = context.getString(R.string.service_account)
        val credential = GoogleCredential
            .fromStream(context.resources.assets.open(credentialFileName))
            .createScoped(scopes)
            ?: return Result.failure()

        val calendarService = GoogleCalendar.Builder(transport, jsonFactory, credential)
            .setApplicationName(context.getString(R.string.app_name))
            .build()
            ?: return Result.failure()

        kotlin.runCatching {
            deleteTodayEvents(calendarService)
        }.onSuccess {
            Log.d(TAG, "doWork: events are successfully deleted")
        }.onFailure { e ->
            Log.e(TAG, "doWork: error while deleting events", e)
            return Result.failure()
        }

        kotlin.runCatching {
            insertAttendances(calendarService, attendances)
        }.onSuccess {
            Log.d(TAG, "doWork: events are successfully inserted")
        }.onFailure { e ->
            Log.e(TAG, "doWork: error while inserting events", e)
            return Result.failure()
        }

        return Result.success()
    }

    private suspend fun deleteTodayEvents(
        calendarService: GoogleCalendar
    ) = withContext(Dispatchers.IO) {
        //今日の分だけ抽出して削除
        var pageToken: String? = null
        do {
            val events = calendarService.events().list(calendarId)
                .setPageToken(pageToken)
                .setTimeMin(DateTime(yesterday.time))
                .setTimeMax(DateTime(tomorrow.time))
                .execute()

            events.items.forEach { event ->
                calendarService.events().delete(calendarId, event.id).execute()
            }
            pageToken = events.nextPageToken
        } while (pageToken != null)
    }

    private suspend fun insertAttendances(
        calendarService: GoogleCalendar,
        attendances: List<UserAttendance>
    ) = withContext(Dispatchers.IO) {
        val events = attendances
            .flatMap { it.toAttendanceList() }
            .map { CalendarData.fromAttendance(it) }
            .map { it.toEvent() }

        events.forEach {
            calendarService.events().insert(calendarId, it).execute()
        }
    }

    companion object {
        private const val TAG = "ExportAttendanceWorker"

        val scopes = listOf(
            Scopes.PROFILE,
            SheetsScopes.SPREADSHEETS,
            CalendarScopes.CALENDAR,
        )

        private fun createWorkerRequest(): PeriodicWorkRequest {
            // WorkManagerの最小インターバルが15分
            return PeriodicWorkRequestBuilder<ExportAttendanceWorker>(
                repeatInterval = 20,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
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