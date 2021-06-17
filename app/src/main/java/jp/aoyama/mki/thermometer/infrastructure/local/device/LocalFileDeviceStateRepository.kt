package jp.aoyama.mki.thermometer.infrastructure.local.device

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import jp.aoyama.mki.thermometer.domain.models.device.DeviceStateEntity
import jp.aoyama.mki.thermometer.domain.repository.DeviceStateRepository
import java.util.*

class LocalFileDeviceStateRepository(
    private val mContext: Context
) : DeviceStateRepository {

    private val mGson = Gson()
    private val mFileInputStream get() = mContext.openFileInput(FILE_NAME)
    private val mFileOutputStream get() = mContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "LocalFileDeviceStateRep"
        private const val FILE_NAME = "device_states.json"
    }

    override suspend fun findAll(): List<DeviceStateEntity> {
        return kotlin.runCatching {
            val statesJson = mFileInputStream.bufferedReader().readLine() ?: "[]"
            mGson.fromJson(statesJson, Array<DeviceStateEntity>::class.java).toList()
        }.fold(
            onSuccess = { it },
            onFailure = { e ->
                Log.e(TAG, "findAll: error while getting device states", e)
                emptyList()
            }
        )
    }

    override suspend fun findInRange(start: Calendar, end: Calendar): List<DeviceStateEntity> {
        return findAll().filter {
            start.timeInMillis < it.createdAt.timeInMillis && it.createdAt.timeInMillis < end.timeInMillis
        }
    }

    override suspend fun findByAddress(address: String): List<DeviceStateEntity> {
        return findAll().filter { it.address == address }
    }

    override suspend fun save(state: DeviceStateEntity) {
        val savedStates = findAll().toMutableList()
        savedStates.add(state)

        val json = mGson.toJson(savedStates)
        mFileOutputStream.use {
            it.write(json.toByteArray())
        }
    }

    override suspend fun delete(id: String) {
        val states = findAll()
            .filter { it.id != id }

        val json = mGson.toJson(states)
        mFileOutputStream.use {
            it.write(json.toByteArray())
        }
    }
}