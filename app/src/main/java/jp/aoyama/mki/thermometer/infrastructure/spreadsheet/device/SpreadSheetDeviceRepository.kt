package jp.aoyama.mki.thermometer.infrastructure.spreadsheet.device

import android.content.Context
import android.util.Log
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.repository.DeviceRepository
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.SpreadSheetUtil

class SpreadSheetDeviceRepository(context: Context) : DeviceRepository {

    private val mSpreadSheet = SpreadSheetUtil(context)

    companion object {
        private const val TAG = "SpreadSheetDeviceReposi"

        // ユーザーの基本情報は A ~ C
        // 端末のMACアドレスを保存する範囲は D ~ Z
        private const val DEVICES_SHEET_RANGE = "users!A:Z"
    }

    private suspend fun deviceSheetRangeOf(userId: String): String? {
        val row = mSpreadSheet.getColumnOf(DEVICES_SHEET_RANGE) {
            it[0] == userId
        } ?: return null

        // 対応するユーザーの行を取得
        return "users!${row}:${row}"
    }

    private suspend fun getAllEntities(): List<SpreadSheetDeviceEntity> {
        return mSpreadSheet.getValues(DEVICES_SHEET_RANGE).mapNotNull {
            SpreadSheetDeviceEntity.fromCSV(it)
        }
    }

    override suspend fun findAll(): List<Device> {
        return getAllEntities().flatMap { it.toDevices() }
    }

    override suspend fun findByUserId(userId: String): List<Device> {
        return findAll().filter { it.userId == userId }
    }

    override suspend fun save(device: Device) {
        // ユーザーを検索
        // ユーザーが保存されていなければ、端末も登録しない
        val user = getAllEntities()
            .find { it.userEntity.id == device.userId } ?: return

        // 端末をデータに追加
        val entity = user.copy(macAddress = user.macAddress + device.address)
        val values = listOf(entity.toCSV())

        val range = deviceSheetRangeOf(device.userId) ?: return
        mSpreadSheet.updateValues(range, values)
    }

    override suspend fun delete(address: String) {
        val user = getAllEntities()
            .find { it.macAddress.contains(address) } ?: return

        // 端末の一覧から、対応する項目を削除
        val devices = user.macAddress.filterNot { it == address }
        val entity = user.copy(macAddress = devices)
        Log.d(TAG, "delete: update to ${entity.toCSV()}")
        val values = listOf(entity.toCSV())

        val range = deviceSheetRangeOf(user.userEntity.id) ?: return
        mSpreadSheet.clearValues(range)
        mSpreadSheet.updateValues(range, values)
    }
}

