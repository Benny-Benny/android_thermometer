package jp.aoyama.mki.thermometer.infrastructure.repositories

import android.content.Context
import jp.aoyama.mki.thermometer.domain.repository.DeviceRepository
import jp.aoyama.mki.thermometer.domain.repository.DeviceStateRepository
import jp.aoyama.mki.thermometer.domain.repository.TemperatureRepository
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.api.bluetooth.ApiDeviceRepository
import jp.aoyama.mki.thermometer.infrastructure.api.bluetooth.ApiDeviceStateRepository
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.device.SpreadSheetDeviceRepository
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.temperature.SpreadSheetBodyTemperatureRepository
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.user.SpreadSheetUserRepository

class RepositoryContainer(private val context: Context) {
    val deviceRepository: DeviceRepository
        get() = DeviceRepositoryImpl(
            baseRepository = SpreadSheetDeviceRepository(context),
            remoteRepository = ApiDeviceRepository(context)
        )

    val deviceStateRepository: DeviceStateRepository
        get() = ApiDeviceStateRepository(context)

    val userRepository: UserRepository
        get() = SpreadSheetUserRepository(context)

    val temperatureRepository: TemperatureRepository
        get() = SpreadSheetBodyTemperatureRepository(context)
}