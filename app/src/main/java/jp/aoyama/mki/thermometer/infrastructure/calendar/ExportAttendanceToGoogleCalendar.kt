package jp.aoyama.mki.thermometer.infrastructure.calendar

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
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
import com.google.api.services.calendar.Calendar as GoogleCalendar

/**
 * Googleカレンダーに出席データを出力する
 */
class ExportAttendanceToGoogleCalendar(
    private val mContext: Context
) {
    private val attendanceService: AttendanceService = AttendanceService(mContext)

    private val calendarId
        get() : String {
            val defaultCalendarId = mContext.getString(R.string.calendar_id)
            // 設定から、CalendarIDが変更されていたら、そちらを利用する。
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
            val keyGoogleCalendarId = mContext.getString(R.string.key_google_calendar_id)
            return sharedPreferences.getString(keyGoogleCalendarId, defaultCalendarId)
                ?: defaultCalendarId
        }

    suspend fun export(
        from: Calendar,
        to: Calendar = Calendar.getInstance()
    ) = withContext(Dispatchers.IO) {
        // Google Calendarサービスの初期化
        val transport = NetHttpTransport.Builder().build()
        val jsonFactory = JacksonFactory.getDefaultInstance()

        val credentialFileName = mContext.getString(R.string.service_account)
        val credential = GoogleCredential
            .fromStream(mContext.resources.assets.open(credentialFileName))
            .createScoped(scopes)
            ?: throw RuntimeException("cannot create GoogleCredential")

        val calendarService = GoogleCalendar.Builder(transport, jsonFactory, credential)
            .setApplicationName(mContext.getString(R.string.app_name))
            .build()
            ?: throw RuntimeException("cannot create GoogleCalendar service")

        // 書き出し対象となる範囲内のカレンダーイベントを削除する
        kotlin.runCatching {
            deleteEvents(calendarService, from, to)
        }.onSuccess {
            Log.d(TAG, "doWork: events are successfully deleted")
        }.onFailure { e ->
            Log.i(TAG, "doWork: error while deleting events", e)
            throw e
        }

        // 書き出し対象となる部分の出席データのみを、GoogleCalendarに出力
        kotlin.runCatching {
            val attendances = attendanceService.getAttendancesOf(from, to)
            insertAttendances(calendarService, attendances)
        }.onSuccess {
            Log.d(TAG, "doWork: events are successfully inserted")
        }.onFailure { e ->
            Log.i(TAG, "doWork: error while inserting events", e)
            throw e
        }

        return@withContext
    }

    private suspend fun deleteEvents(
        calendarService: GoogleCalendar,
        from: Calendar,
        to: Calendar
    ) = withContext(Dispatchers.IO) {
        //今日の分だけ抽出して削除
        var pageToken: String? = null
        do {
            val events = calendarService.events().list(calendarId)
                .setPageToken(pageToken)
                .setTimeMin(DateTime(from.time))
                .setTimeMax(DateTime(to.time))
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
        // 出席データから、Googleカレンダーのイベントを作成
        val events = attendances
            .flatMap { it.toAttendanceList() }
            .map { CalendarData.fromAttendance(it) }
            .map { it.toEvent() }

        events.forEach {
            calendarService.events().insert(calendarId, it).execute()
        }
    }

    companion object {
        private const val TAG = "AttendanceGoogleCalenda"

        val scopes = listOf(
            Scopes.PROFILE,
            SheetsScopes.SPREADSHEETS,
            CalendarScopes.CALENDAR,
        )
    }
}