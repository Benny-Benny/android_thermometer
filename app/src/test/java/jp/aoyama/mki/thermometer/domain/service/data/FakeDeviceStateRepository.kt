package jp.aoyama.mki.thermometer.domain.service.data

import jp.aoyama.mki.thermometer.domain.models.DeviceStateEntity
import jp.aoyama.mki.thermometer.domain.repository.DeviceStateRepository

class FakeDeviceStateRepository : DeviceStateRepository {
    private val baseRepository = FakeBaseRepository<String, DeviceStateEntity>()

    override suspend fun findAll(): List<DeviceStateEntity> {
        return baseRepository.findALl()
    }

    override suspend fun findByAddress(address: String): List<DeviceStateEntity> {
        return findAll().filter { it.address == address }
    }

    override suspend fun save(state: DeviceStateEntity) {
        baseRepository.save(state.id, state)
    }

    override suspend fun delete(state: DeviceStateEntity) {
        baseRepository.delete(state.id)
    }

    fun clear() {
        baseRepository.clear()
    }
}