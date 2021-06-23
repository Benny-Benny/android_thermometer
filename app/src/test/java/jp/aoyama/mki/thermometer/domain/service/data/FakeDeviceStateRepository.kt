package jp.aoyama.mki.thermometer.domain.service.data

import jp.aoyama.mki.thermometer.domain.models.device.DeviceStateEntity
import jp.aoyama.mki.thermometer.domain.repository.DeviceStateRepository
import java.util.*

class FakeDeviceStateRepository : DeviceStateRepository {
    private val baseRepository = FakeBaseRepository<String, DeviceStateEntity>()

    override suspend fun findAll(): List<DeviceStateEntity> {
        return baseRepository.findALl()
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
        baseRepository.save(state.id, state)
    }

    override suspend fun delete(id: String) {
        baseRepository.delete(id)
    }

    fun clear() {
        baseRepository.clear()
    }
}