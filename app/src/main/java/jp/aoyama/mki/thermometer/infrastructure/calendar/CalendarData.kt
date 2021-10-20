package jp.aoyama.mki.thermometer.infrastructure.calendar

import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import jp.aoyama.mki.thermometer.domain.models.attendance.Attendance
import java.text.SimpleDateFormat
import java.util.*
import com.google.api.client.util.DateTime as GoogleCalendarDateTime

/**
 * Google Calendarに書き込むデータ
 */
data class CalendarData(
    val start: Calendar,
    val end: Calendar,
    val isAttending: Boolean,
    val name: String,
) {
    companion object {
        fun fromAttendance(attendance: Attendance): CalendarData {
            return CalendarData(
                start = attendance.enterAt,
                end = attendance.leftAt ?: Calendar.getInstance(),
                isAttending = attendance.leftAt == null,
                name = attendance.userName
            )
        }
    }

    /**
     * Google Calendarに書込み可能なEvent型に変換する
     */
    fun toEvent(): Event {
        val event = Event()

        //イベントのタイトル設定
        event.summary = if (isAttending) "$name (在籍中)" else name
        if (isAttending) event.colorId = "1"

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.JAPAN)

        //イベントの開始時刻設定
        val formattedStart = formatter.format(start.time)
        val startDateTime = GoogleCalendarDateTime(formattedStart)
        event.start = EventDateTime()
            .setDateTime(startDateTime)
            .setTimeZone("Asia/Tokyo")

        //イベントの終了時刻設定
        val formattedEnd = formatter.format(end.time)
        val endDateTime = GoogleCalendarDateTime(formattedEnd)
        event.end = EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone("Asia/Tokyo")

        return event
    }
}