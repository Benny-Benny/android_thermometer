package jp.aoyama.mki.thermometer.domain.service.data

import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.repository.DeviceRepository

class FakeDeviceRepository : DeviceRepository {
    private val baseRepository = FakeBaseRepository<String, Device>()

    override suspend fun findAll(): List<Device> {
        return baseRepository.findALl()
    }

    override suspend fun findByUserId(userId: String): List<Device> {
        return findAll().filter { it.userId == userId }
    }

    override suspend fun save(device: Device) {
        baseRepository.save(device.address, device)
    }

    override suspend fun delete(address: String) {
        baseRepository.delete(address)
    }

    fun clear() {
        baseRepository.clear()
    }
}