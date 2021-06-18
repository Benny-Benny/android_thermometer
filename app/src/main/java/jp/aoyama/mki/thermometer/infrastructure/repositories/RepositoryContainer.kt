package jp.aoyama.mki.thermometer.infrastructure.repositories

import android.content.Context
import jp.aoyama.mki.thermometer.domain.repository.DeviceRepository
import jp.aoyama.mki.thermometer.domain.repository.DeviceStateRepository
import jp.aoyama.mki.thermometer.domain.repository.TemperatureRepository
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.api.bluetooth.ApiDeviceRepository
import jp.aoyama.mki.thermometer.infrastructure.api.bluetooth.ApiDeviceStateRepository
import jp.aoyama.mki.thermometer.infrastructure.local.device.LocalFileDeviceRepository
import jp.aoyama.mki.thermometer.infrastructure.local.temperature.LocalFileBodyTemperatureRepository
import jp.aoyama.mki.thermometer.infrastructure.local.user.LocalFileUserRepository

class RepositoryContainer(private val context: Context) {
    val deviceRepository: DeviceRepository
        get() = DeviceRepositoryImpl(
            localRepository = LocalFileDeviceRepository(context),
            remoteRepository = ApiDeviceRepository(context)
        )

    val deviceStateRepository: DeviceStateRepository
        get() = ApiDeviceStateRepository(context)

    val userRepository: UserRepository
        get() = LocalFileUserRepository(context)

    val temperatureRepository: TemperatureRepository
        get() = LocalFileBodyTemperatureRepository(context)
}