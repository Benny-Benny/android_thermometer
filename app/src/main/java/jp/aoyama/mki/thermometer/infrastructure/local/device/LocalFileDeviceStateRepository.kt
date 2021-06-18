package jp.aoyama.mki.thermometer.infrastructure.local.device

import android.content.Context
import jp.aoyama.mki.thermometer.domain.models.device.DeviceStateEntity
import jp.aoyama.mki.thermometer.domain.repository.DeviceStateRepository
import jp.aoyama.mki.thermometer.infrastructure.local.LocalFileBaseRepository
import java.util.*

class LocalFileDeviceStateRepository(
    context: Context
) : DeviceStateRepository {

    private val mBaseRepository = LocalFileBaseRepository(
        context,
        FILE_NAME,
    ) { gson, json ->
        gson.fromJson(json, Array<DeviceStateEntity>::class.java).toList()
    }

    companion object {
        private const val FILE_NAME = "device_states.json"
    }

    override suspend fun findAll(): List<DeviceStateEntity> {
        return mBaseRepository.findAll()
    }

    override suspend fun findInRange(start: Calendar, end: Calendar): List<DeviceStateEntity> {
        return mBaseRepository.findAll {
            start.timeInMillis < it.createdAt.timeInMillis && it.createdAt.timeInMillis < end.timeInMillis
        }
    }

    override suspend fun findByAddress(address: String): List<DeviceStateEntity> {
        return mBaseRepository.findAll { it.address == address }
    }

    override suspend fun save(state: DeviceStateEntity) {
        mBaseRepository.save(state)
    }

    override suspend fun delete(id: String) {
        mBaseRepository.delete { it.id == id }
    }
}