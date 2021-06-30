package jp.aoyama.mki.thermometer.infrastructure.workmanager

import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import jp.aoyama.mki.thermometer.domain.models.attendance.Attendance
import java.text.SimpleDateFormat
import java.util.*
import com.google.api.client.util.DateTime as GoogleCalendarDateTime

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
                isAttending = false,
                name = attendance.userName
            )
        }
    }

    fun toEvent(): Event {
        //イベントのタイトル設定
        val event = Event()
            .setSummary(name)

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

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

        //イベントの説明設定（在籍中かどうか）
        if (isAttending) {
            event.description = "在籍中"
        }

        return event
    }
}