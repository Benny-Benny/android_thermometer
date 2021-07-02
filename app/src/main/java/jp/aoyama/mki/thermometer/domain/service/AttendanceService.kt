package jp.aoyama.mki.thermometer.domain.service

import android.content.Context
import jp.aoyama.mki.thermometer.domain.models.attendance.AttendanceEntity
import jp.aoyama.mki.thermometer.domain.models.attendance.UserAttendance
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.device.DeviceStateEntity
import jp.aoyama.mki.thermometer.domain.repository.DeviceStateRepository
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.repositories.RepositoryContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class AttendanceService(
    private val userRepository: UserRepository,
    private val deviceStateRepository: DeviceStateRepository
) {

    constructor(context: Context) : this(
        userRepository = RepositoryContainer(context).userRepository,
        deviceStateRepository = RepositoryContainer(context).deviceStateRepository
    )

    suspend fun getAttendances(): List<UserAttendance> = withContext(Dispatchers.IO) {
        val users = userRepository.findAll()
        val states = deviceStateRepository.findAll()

        return@withContext users.mapNotNull { user ->
            val device = user.device ?: return@mapNotNull null
            getUserAttendance(
                user.id,
                user.name,
                device,
                states
            )
        }
    }

    /**
     *  指定した日付の範囲内の出席データを取得
     */
    suspend fun getAttendancesOf(start: Calendar, end: Calendar): List<UserAttendance> =
        withContext(Dispatchers.IO) {
            val users = userRepository.findAll()
            val states = deviceStateRepository.findInRange(start, end)

            return@withContext users.mapNotNull { user ->
                val device = user.device ?: return@mapNotNull null
                val deviceStates = states.filter { it.address == device.address }
                getUserAttendance(
                    user.id,
                    user.name,
                    device,
                    deviceStates
                )
            }
        }

    /**
     * 指定したユーザーの出席記録をすべて取得
     */
    suspend fun getUserAttendance(userId: String, userName: String): UserAttendance {
        val device = userRepository.find(userId)?.device
            ?: return UserAttendance(
                userId,
                userName,
                attendances = emptyList()
            )
        val userDeviceStates = deviceStateRepository.findByAddress(device.address)
        return getUserAttendance(userId, userName, device, userDeviceStates)
    }

    private fun getUserAttendance(
        userId: String,
        userName: String,
        device: Device,
        deviceStates: List<DeviceStateEntity>
    ): UserAttendance {
        if (device.userId != userId) throw Exception("Device#userId is not same as $userId")

        // 端末の検索記録を、日時順にソート
        val sortedStates = deviceStates
            .filter { it.address == device.address }
            .sortedBy { it.createdAt.timeInMillis }

        // 中間の結果を破棄する
        val removedMiddleStates = sortedStates.mapIndexedNotNull { index, currentState ->
            if (index == 0)
                return@mapIndexedNotNull currentState

            val prevState = sortedStates[index - 1]
            if (prevState.found != currentState.found) currentState
            else null
        }

        // 入室と退室履歴の紐付けを行う
        val foundStates = removedMiddleStates.filter { it.found }
        val attendances = foundStates.map { enterState ->
            val nextLeftState = removedMiddleStates
                .filter { !it.found }
                .firstOrNull { it.createdAt.timeInMillis >= enterState.createdAt.timeInMillis }

            AttendanceEntity(
                userId = userId,
                enterAt = enterState.createdAt,
                leftAt = nextLeftState?.createdAt
            )
        }

        return UserAttendance(
            userId = userId,
            userName = userName,
            attendances = attendances
        )
    }

    companion object {
        private const val TAG = "AttendanceService"
    }
}