package jp.aoyama.mki.thermometer.domain.service.data

class FakeBaseRepository<K, V> {
    val data = mutableMapOf<K, V>()

    fun findALl(): List<V> {
        return data.values.toList()
    }

    fun find(key: K): V? {
        return data[key]
    }

    fun save(key: K, data: V) {
        this.data[key] = data
    }

    fun delete(key: K) {
        data.remove(key)
    }

    fun clear() {
        data.clear()
    }
}