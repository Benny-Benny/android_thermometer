package jp.aoyama.mki.thermometer.infrastructure.local.device

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import jp.aoyama.mki.thermometer.domain.models.Device
import jp.aoyama.mki.thermometer.domain.repository.DeviceRepository

class LocalFileDeviceRepository(
    private val mContext: Context
) : DeviceRepository {

    private val mGson = Gson()
    private val mFileInputStream get() = mContext.openFileInput(FILE_NAME)
    private val mFileOutputStream get() = mContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)


    companion object {
        private const val TAG = "LocalFileDeviceReposito"
        private const val FILE_NAME = "devices.json"
    }

    override suspend fun findAll(): List<Device> {
        return kotlin.runCatching {
            val devicesJson = mFileInputStream.bufferedReader().readLine() ?: "[]"
            mGson.fromJson(devicesJson, Array<Device>::class.java).toList()
        }.fold(
            onSuccess = { it },
            onFailure = { e ->
                Log.e(TAG, "findAll: error while getting devices", e)
                emptyList()
            }
        )
    }

    override suspend fun findByUserId(userId: String): List<Device> {
        return findAll().filter { it.userId == userId }
    }

    override suspend fun save(device: Device) {
        val savedDevices = findAll().toMutableList()
        savedDevices.add(device)

        val json = mGson.toJson(savedDevices)
        mFileOutputStream.use {
            it.write(json.toByteArray())
        }
    }

    override suspend fun delete(address: String) {
        val devices = findAll()
            .filter { it.address != address }

        val json = mGson.toJson(devices)
        mFileOutputStream.use {
            it.write(json.toByteArray())
        }
    }

    override suspend fun deleteAllByUserId(userId: String) {
        val devices = findAll()
            .filter { it.userId != userId }

        val json = mGson.toJson(devices)
        mFileOutputStream.use {
            it.write(json.toByteArray())
        }
    }
}