package jp.aoyama.mki.thermometer.domain.service

import jp.aoyama.mki.thermometer.domain.models.attendance.AttendanceEntity
import jp.aoyama.mki.thermometer.domain.models.attendance.UserAttendance
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.device.DeviceStateEntity
import jp.aoyama.mki.thermometer.domain.models.user.UserEntity
import jp.aoyama.mki.thermometer.domain.service.data.FakeDeviceRepository
import jp.aoyama.mki.thermometer.domain.service.data.FakeDeviceStateRepository
import jp.aoyama.mki.thermometer.domain.service.data.FakeUserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

@ExperimentalCoroutinesApi
class AttendanceServiceTest {
    private val userRepository = FakeUserRepository()
    private val deviceRepository = FakeDeviceRepository()
    private val deviceStateRepository = FakeDeviceStateRepository()
    private val attendanceService =
        AttendanceService(userRepository, deviceRepository, deviceStateRepository)
    private val testUser = UserEntity(id = UUID.randomUUID().toString(), name = "test")
    private val testUserDevice = Device(userId = testUser.id, address = "test")

    @BeforeEach
    fun setUp() {
        userRepository.clear()
        deviceRepository.clear()
        deviceRepository.clear()
    }

    @Test
    fun `出席データを取得`() = runBlockingTest {
        /*
        初期データ: [
            DeviceState(at=12:00, found=True)
            DeviceState(at=13:00, found=False)
            DeviceState(at=14:00, found=True)
            DeviceState(at=15:00, found=False)
        ]
        期待する結果: [
            Attendance(enterAt=12:00, leftAt=13:00),
            Attendance(enterAt=14:00, leftAt=15:00)
        ]
         */
        userRepository.save(testUser)
        deviceRepository.save(testUserDevice)

        val states = listOf(
            DeviceStateEntity(
                address = testUserDevice.address,
                found = true,
                createdAt = getDay(hour = 12)
            ),
            DeviceStateEntity(
                address = testUserDevice.address,
                found = false,
                createdAt = getDay(hour = 13)
            ),
            DeviceStateEntity(
                address = testUserDevice.address,
                found = true,
                createdAt = getDay(hour = 14)
            ),
            DeviceStateEntity(
                address = testUserDevice.address,
                found = false,
                createdAt = getDay(hour = 15)
            )
        )

        states.forEach { deviceStateRepository.save(it) }

        val attendance = attendanceService.getUserAttendance(testUser.id, testUser.name)
        Assertions.assertEquals(
            UserAttendance(
                userId = testUser.id,
                userName = testUser.name,
                attendances = listOf(
                    AttendanceEntity(
                        testUser.id,
                        enterAt = states[0].createdAt,
                        leftAt = states[1].createdAt
                    ),
                    AttendanceEntity(
                        testUser.id,
                        enterAt = states[2].createdAt,
                        leftAt = states[3].createdAt
                    )
                )
            ),
            attendance
        )
    }

    @Test
    fun `中間のデータを無視する`() = runBlockingTest {
        /*
        初期データ: [
            DeviceState(at=12:00, found=True)
            DeviceState(at=14:00, found=True)
            DeviceState(at=15:00, found=False)
            DeviceState(at=16:00, found=False)
        ]
        期待する結果: Attendance(enterAt=12:00, leftAt=15:00)
         */
        userRepository.save(testUser)
        deviceRepository.save(testUserDevice)
        val enterState = DeviceStateEntity(
            address = testUserDevice.address,
            found = true,
            createdAt = getDay(hour = 12)
        )
        val additionalEnterState = DeviceStateEntity(
            address = testUserDevice.address,
            found = true,
            createdAt = getDay(hour = 13)
        )
        val leftState = DeviceStateEntity(
            address = testUserDevice.address,
            found = false,
            createdAt = getDay(hour = 14)
        )
        val additionalLeftState = DeviceStateEntity(
            address = testUserDevice.address,
            found = false,
            createdAt = getDay(hour = 15)
        )
        deviceStateRepository.save(enterState)
        deviceStateRepository.save(additionalEnterState)
        deviceStateRepository.save(leftState)
        deviceStateRepository.save(additionalLeftState)

        val attendance = attendanceService.getUserAttendance(testUser.id, testUser.name)
        Assertions.assertEquals(
            UserAttendance(
                userId = testUser.id,
                userName = testUser.name,
                attendances = listOf(
                    AttendanceEntity(
                        testUser.id,
                        enterAt = enterState.createdAt,
                        leftAt = leftState.createdAt
                    )
                )
            ),
            attendance
        )
    }

    @Test
    fun `まだ退出していない場合の出席データを取得`() = runBlockingTest {
        /*
        初期データ: [
            DeviceState(address=test,  at=12:00, found=True)
            DeviceState(address=test,  at=13:00, found=True)
        ]
        期待する結果: Attendance(enterAt=12:00, leftAt=null)
         */
        userRepository.save(testUser)
        deviceRepository.save(testUserDevice)
        val states = listOf(
            DeviceStateEntity(
                address = testUserDevice.address,
                found = true,
                createdAt = getDay(hour = 12)
            ),
            DeviceStateEntity(
                address = testUserDevice.address,
                found = true,
                createdAt = getDay(hour = 13)
            )
        )
        states.forEach { deviceStateRepository.save(it) }

        val attendance = attendanceService.getUserAttendance(testUser.id, testUser.name)
        Assertions.assertEquals(
            UserAttendance(
                userId = testUser.id,
                userName = testUser.name,
                attendances = listOf(
                    AttendanceEntity(
                        testUser.id,
                        enterAt = states.first().createdAt,
                        leftAt = null
                    )
                )
            ),
            attendance
        )
    }

    private fun getDay(
        year: Int = 2000,
        month: Int = 1,
        day: Int = 1,
        hour: Int = 12,
        minute: Int = 0,
        second: Int = 0
    ): Calendar {
        return Calendar.getInstance()
            .apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, second)
            }
    }
}