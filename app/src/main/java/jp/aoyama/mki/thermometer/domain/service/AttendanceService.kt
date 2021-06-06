package jp.aoyama.mki.thermometer.domain.service

import jp.aoyama.mki.thermometer.domain.models.AttendanceEntity
import jp.aoyama.mki.thermometer.domain.models.UserAttendance
import jp.aoyama.mki.thermometer.domain.repository.DeviceRepository
import jp.aoyama.mki.thermometer.domain.repository.DeviceStateRepository
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AttendanceService(
    private val userRepository: UserRepository,
    private val deviceRepository: DeviceRepository,
    private val deviceStateRepository: DeviceStateRepository
) {
    suspend fun getAttendances(): List<UserAttendance> = withContext(Dispatchers.IO) {
        val users = userRepository.findAll()
        return@withContext users.map { user -> getUserAttendance(user.id, user.name) }
    }

    suspend fun getUserAttendance(userId: String, userName: String): UserAttendance {
        val userDevices = deviceRepository.findByUserId(userId)

        // ユーザーに紐づくすべての端末の検索記録を、日時順にソート
        val deviceAllStates = userDevices
            .flatMap { deviceStateRepository.findByAddress(it.address) }
            .sortedBy { it.createdAt.timeInMillis }

        val deviceStates = deviceAllStates.mapIndexedNotNull { index, currentState ->
            if (index == 0 || index == deviceAllStates.size - 1)
                return@mapIndexedNotNull currentState

            // 発見状態の変化しない途中の情報は破棄する
            val prevState = deviceAllStates[index - 1]
            val nextState = deviceAllStates[index + 1]

            // 前回の検索で端末を発見し、今回も発見できていたら前回のデータを残す
            if (currentState.found && prevState.found) null
            // 今回の検索で端末を発見できず、次回も発見できなかったら、次回のデータを残す
            else if (!currentState.found && !nextState.found) null
            else currentState
        }

        val foundStates = deviceStates.filter { it.found }
        val attendances = foundStates.mapNotNull { enterState ->
            val nextLeftState = deviceStates.firstOrNull { !it.found } ?: return@mapNotNull null
            AttendanceEntity(
                userId = userId,
                enterAt = enterState.createdAt,
                leftAt = nextLeftState.createdAt
            )
        }

        return UserAttendance(
            userId = userId,
            userName = userName,
            attendances = attendances
        )
    }
}