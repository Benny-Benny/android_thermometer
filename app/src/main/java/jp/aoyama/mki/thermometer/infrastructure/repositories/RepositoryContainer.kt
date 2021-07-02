package jp.aoyama.mki.thermometer.infrastructure.repositories

import android.content.Context
import jp.aoyama.mki.thermometer.domain.repository.DeviceStateRepository
import jp.aoyama.mki.thermometer.domain.repository.TemperatureRepository
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.api.bluetooth.devices.ApiDeviceRepository
import jp.aoyama.mki.thermometer.infrastructure.api.bluetooth.state.ApiDeviceStateRepository
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.temperature.SpreadSheetBodyTemperatureRepository
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.user.SpreadSheetUserRepository

/**
 * ServiceやViewModelが依存するRepositoryを一括で管理する。
 *
 * インターフェースに従った実装を作成し、RepositoryContainerにより指定すると
 * このアプリケーション内で利用されるデータ管理の実装への依存を
 * 一括で変更することができる。
 */
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