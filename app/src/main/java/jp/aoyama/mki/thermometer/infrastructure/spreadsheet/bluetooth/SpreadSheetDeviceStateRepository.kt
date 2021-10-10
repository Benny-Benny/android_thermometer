package jp.aoyama.mki.thermometer.infrastructure.spreadsheet.bluetooth

import android.content.Context
import jp.aoyama.mki.thermometer.domain.models.device.DeviceStateEntity
import jp.aoyama.mki.thermometer.domain.repository.DeviceStateRepository
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.SpreadSheetUtil
import java.util.*

class SpreadSheetDeviceStateRepository(context: Context) : DeviceStateRepository {
    companion object {
        private const val ATTENDANCE_SHEET_RANGE = "attendance!A:D"
    }

    private val mSpreadSheet = SpreadSheetUtil(context)


    override suspend fun findAll(): List<DeviceStateEntity> {
        return mSpreadSheet.getValues(ATTENDANCE_SHEET_RANGE)
            .mapNotNull { SpreadSheetDeviceStateEntity.fromCSV(it) }
            .map { it.toDeviceState() }
    }

    override suspend fun findInRange(start: Calendar, end: Calendar): List<DeviceStateEntity> {
        return findAll().filter {
            start.timeInMillis <= it.createdAt.timeInMillis && it.createdAt.timeInMillis <= end.timeInMillis
        }
    }

    override suspend fun findByAddress(address: String): List<DeviceStateEntity> {
        return findAll().filter { it.address == address }
    }

    override suspend fun findLatest(address: String): DeviceStateEntity? {
        return findByAddress(address).maxByOrNull { it.createdAt.timeInMillis }
    }

    override suspend fun save(state: DeviceStateEntity) {
        /* no-op */
    }
}