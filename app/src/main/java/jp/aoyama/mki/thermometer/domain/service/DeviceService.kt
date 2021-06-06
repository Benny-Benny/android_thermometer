package jp.aoyama.mki.thermometer.domain.service

import android.content.Context
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.device.DeviceStateEntity
import jp.aoyama.mki.thermometer.domain.repository.DeviceRepository
import jp.aoyama.mki.thermometer.domain.repository.DeviceStateRepository
import jp.aoyama.mki.thermometer.infrastructure.repositories.RepositoryContainer

class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val stateRepository: DeviceStateRepository,
) {
    constructor(context: Context) : this(
        deviceRepository = RepositoryContainer(context).deviceRepository,
        stateRepository = RepositoryContainer(context).deviceStateRepository
    )

    suspend fun getDevices(): List<Device> {
        return deviceRepository.findAll()
    }

    suspend fun updateDeviceState(state: DeviceStateEntity) {
        val lastDeviceState = stateRepository
            .findByAddress(state.address)
            .sortedBy { it.createdAt.timeInMillis }
            .lastOrNull()

        // 初回のデータだったら必ず保存する
        if (lastDeviceState == null) {
            stateRepository.save(state)
            return
        }

        if (lastDeviceState.found != state.found) {
            // 状態に変化があったら更新する
            stateRepository.save(state)
            return
        }

        // 前回も発見状態なら、保存しない
        if (lastDeviceState.found && state.found) return

        // 前回も端末を発見できなかったら、今回のデータで更新する
        if (!lastDeviceState.found && !state.found) {
            stateRepository.delete(lastDeviceState.id)
            stateRepository.save(state)
        }
    }
}