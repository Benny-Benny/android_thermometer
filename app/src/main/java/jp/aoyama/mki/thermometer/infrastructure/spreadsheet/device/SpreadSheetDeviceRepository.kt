package jp.aoyama.mki.thermometer.infrastructure.spreadsheet.device

import android.content.Context
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.repository.DeviceRepository
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.SpreadSheetUtil

class SpreadSheetDeviceRepository(context: Context) : DeviceRepository {

    private val mSpreadSheet = SpreadSheetUtil(context)

    private suspend fun getAllEntities(): List<SpreadSheetDeviceEntity> {
        return mSpreadSheet.getValues(DEVICES_SHEET_RANGE).mapNotNull {
            SpreadSheetDeviceEntity.fromCSV(it)
        }
    }

    override suspend fun findAll(): List<Device> {
        return getAllEntities().map { it.toDevice() }
    }

    override suspend fun findByUserId(userId: String): List<Device> {
        return findAll().filter { it.userId == userId }
    }

    override suspend fun save(device: Device) {
        delete(device.address)

        val entity = SpreadSheetDeviceEntity.fromDevice(device)
        val values = listOf(entity.toCSV())
        mSpreadSheet.appendValues(DEVICES_SHEET_RANGE, values)
    }

    override suspend fun delete(address: String) {
        val devices = getAllEntities().toMutableList()
        devices.removeAll { it.macAddress == address }
        devices.sortBy { it.userId.toInt() }

        val values = devices.map { it.toCSV() }
        mSpreadSheet.clearValues(DEVICES_SHEET_RANGE)
        mSpreadSheet.appendValues(DEVICES_SHEET_RANGE, values)
    }

    companion object {
        private const val DEVICES_SHEET_RANGE = "devices!A:B"
        private const val TAG = "SpreadSheetDeviceReposi"
    }
}

