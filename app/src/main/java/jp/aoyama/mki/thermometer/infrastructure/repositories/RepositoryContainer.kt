package jp.aoyama.mki.thermometer.infrastructure.repositories

import android.content.Context
import jp.aoyama.mki.thermometer.domain.repository.DeviceRepository
import jp.aoyama.mki.thermometer.domain.repository.DeviceStateRepository
import jp.aoyama.mki.thermometer.domain.repository.TemperatureRepository
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.api.bluetooth.ApiDeviceRepository
import jp.aoyama.mki.thermometer.infrastructure.api.temperature.ApiTemperatureRepository
import jp.aoyama.mki.thermometer.infrastructure.api.user.ApiUserRepository
import jp.aoyama.mki.thermometer.infrastructure.local.device.LocalFileDeviceStateRepository

class RepositoryContainer(private val context: Context) {
    val deviceRepository: DeviceRepository get() = ApiDeviceRepository()
    val deviceStateRepository: DeviceStateRepository get() = LocalFileDeviceStateRepository(context)
    val userRepository: UserRepository get() = ApiUserRepository()
    val temperatureRepository: TemperatureRepository get() = ApiTemperatureRepository()
}