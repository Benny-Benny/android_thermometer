package jp.aoyama.mki.thermometer.infrastructure.repositories

import android.content.Context
import jp.aoyama.mki.thermometer.domain.repository.DeviceStateRepository
import jp.aoyama.mki.thermometer.domain.repository.TemperatureRepository
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.api.bluetooth.ApiDeviceRepository
import jp.aoyama.mki.thermometer.infrastructure.api.bluetooth.ApiDeviceStateRepository
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.temperature.SpreadSheetBodyTemperatureRepository
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.user.SpreadSheetUserRepository

class RepositoryContainer(private val context: Context) {
    val deviceStateRepository: DeviceStateRepository
        get() = ApiDeviceStateRepository(context)

    val userRepository: UserRepository
        get() = UserRepositoryImpl(
            userRepository = SpreadSheetUserRepository(context),
            deviceRepository = ApiDeviceRepository(context)
        )

    val temperatureRepository: TemperatureRepository
        get() = SpreadSheetBodyTemperatureRepository(context, userRepository)
}