package jp.aoyama.mki.thermometer.infrastructure.local.device

import android.content.Context
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.repository.DeviceRepository
import jp.aoyama.mki.thermometer.infrastructure.local.LocalFileBaseRepository

class LocalFileDeviceRepository(
    context: Context
) : DeviceRepository {

    private val mBaseRepository = LocalFileBaseRepository(
        context,
        FILE_NAME
    ) { gson, json ->
        gson.fromJson(json, Array<Device>::class.java).toList()
    }

    companion object {
        private const val FILE_NAME = "devices.json"
    }

    override suspend fun findAll(): List<Device> {
        return mBaseRepository.findAll()
    }

    override suspend fun findByUserId(userId: String): List<Device> {
        return mBaseRepository.findAll { device ->
            device.userId == userId
        }
    }

    override suspend fun save(device: Device) {
        mBaseRepository.save(device)
    }

    override suspend fun delete(address: String) {
        mBaseRepository.delete { device ->
            device.address == address
        }
    }
}