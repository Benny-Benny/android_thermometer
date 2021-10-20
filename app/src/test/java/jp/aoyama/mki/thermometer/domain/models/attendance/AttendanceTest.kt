package jp.aoyama.mki.thermometer.domain.models.attendance

import jp.aoyama.mki.thermometer.domain.models.attendance.Attendance.Companion.compress
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class AttendanceTest {

    data class CompressTestCase(
        val prev: Attendance,
        val next: Attendance,
        val expected: Attendance?,
        val message: String
    )

    @Test
    fun testCompress() {
        val userId = "test"
        val userName = "test"

        val enteredAt = Calendar.getInstance()
        val leftAt = Calendar.getInstance().apply {
            timeInMillis = enteredAt.timeInMillis + 5 * 60 * 1000
        }
        val fiveMinutesAfter = Calendar.getInstance().apply {
            timeInMillis = leftAt.timeInMillis + 5 * 60 * 1000
        }
        val twentyMinutesAfter = Calendar.getInstance().apply {
            timeInMillis = leftAt.timeInMillis + 20 * 60 * 1000
        }

        val tests = listOf(
            CompressTestCase(
                Attendance(userId, userName, enteredAt, leftAt),
                Attendance(userId, userName, fiveMinutesAfter, null),
                Attendance(userId, userName, enteredAt, null),
                "前の退出時間と次の入室時間の差が10分未満の場合は、一つにまとめる"
            ),
            CompressTestCase(
                Attendance(userId, userName, enteredAt, leftAt),
                Attendance(userId, userName, twentyMinutesAfter, null),
                null,
                "前の退出時間と次の入室時間の差が10分未満の場合は、一つにまとめる"
            )
        )

        tests.forEach {
            assertEquals(
                it.expected?.enterAt?.timeInMillis,
                compress(it.prev, it.next)?.enterAt?.timeInMillis,
                it.message
            )
            assertEquals(
                it.expected?.leftAt?.timeInMillis,
                compress(it.prev, it.next)?.leftAt?.timeInMillis,
                it.message
            )
        }
    }

    data class CompressAttendancesTestCase(
        val attendances: List<Attendance>,
        val expected: List<Attendance>,
        val message: String
    )

    @Test
    fun testCompressAttendances() {
        val userId = "test"
        val userName = "test"

        val enteredAt = Calendar.getInstance()
        val leftAt = Calendar.getInstance().apply {
            timeInMillis = enteredAt.timeInMillis + 5 * 60 * 1000
        }
        val tests = listOf(
            CompressAttendancesTestCase(
                listOf(
                    Attendance(userId, userName, enteredAt, leftAt),
                    Attendance(userId, userName, leftAt.addMinutes(5), leftAt.addMinutes(10)),
                    Attendance(userId, userName, leftAt.addMinutes(15), null),
                ),
                expected = listOf(Attendance(userId, userName, enteredAt, null)),
                "前の退出時間と次の入室時間の差が10分未満の場合は、一つにまとめる"
            ),
            CompressAttendancesTestCase(
                listOf(
                    Attendance(userId, userName, enteredAt, leftAt),
                    Attendance(userId, userName, leftAt.addMinutes(20), leftAt.addMinutes(30)),
                    Attendance(userId, userName, leftAt.addMinutes(50), leftAt.addMinutes(60)),
                ),
                expected = listOf(
                    Attendance(userId, userName, enteredAt, leftAt),
                    Attendance(userId, userName, leftAt.addMinutes(20), leftAt.addMinutes(30)),
                    Attendance(userId, userName, leftAt.addMinutes(50), leftAt.addMinutes(60)),
                ),
                "前後の入退室時間が、10分以上離れている場合はまとめない"
            ),
        )

        tests.forEach {
            val compressed = it.attendances.compress(userId)
            assertEquals(it.expected.size, compressed.size)

            compressed.forEachIndexed { index, attendance ->
                val expected = it.expected[index]
                assertEquals(
                    expected.enterAt.timeInMillis,
                    attendance.enterAt.timeInMillis,
                    it.message
                )
                assertEquals(
                    expected.leftAt?.timeInMillis,
                    attendance.leftAt?.timeInMillis,
                    it.message
                )
            }
        }
    }

    private fun Calendar.addMinutes(minute: Int): Calendar {
        return Calendar.getInstance().let {
            it.timeInMillis = timeInMillis + minute * 60 * 1000
            it
        }
    }
}