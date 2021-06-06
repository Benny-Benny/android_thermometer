package jp.aoyama.mki.thermometer.view.temperature.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import jp.aoyama.mki.thermometer.domain.models.BodyTemperatureEntity
import jp.aoyama.mki.thermometer.domain.models.TemperatureData
import jp.aoyama.mki.thermometer.infrastructure.local.temperature.LocalFileTemperatureRepository
import jp.aoyama.mki.thermometer.infrastructure.local.user.LocalFileUserRepository
import jp.aoyama.mki.thermometer.infrastructure.local.user.TemperatureCSVUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class TemperatureViewModel : ViewModel() {
    suspend fun getTemperatureData(context: Context): List<TemperatureData> {
        val temperatureRepository = LocalFileTemperatureRepository(context)
        val userRepository = LocalFileUserRepository(context)

        return withContext(Dispatchers.IO) {
            val users = userRepository.findAll()
            temperatureRepository.findAll()
                .mapNotNull { entity ->
                    val user = users.find { it.id == entity.userId } ?: return@mapNotNull null
                    entity.toTemperatureData(user.name)
                }
                .sortedBy { it.createdAt.timeInMillis }
                .reversed()
        }
    }

    /**
     * 入力された体温を保存
     * @param userId 体温を記録する人のID
     * @param temperature 体温
     * @return 入力値が適切であれば true を返す
     */
    suspend fun saveTemperature(context: Context, userId: String, temperature: Float?): Boolean {
        if (temperature == null) return false
        if (temperature > 45f || temperature < 35f) return false

        val data = BodyTemperatureEntity(
            userId = userId,
            temperature = temperature,
            createdAt = Calendar.getInstance()
        )
        withContext(Dispatchers.IO) {
            val temperatureRepository = LocalFileTemperatureRepository(context)
            temperatureRepository.save(data)
        }

        return true
    }

    suspend fun exportCSV(context: Context): Uri {
        val data = getTemperatureData(context)
        return withContext(Dispatchers.IO) {
            TemperatureCSVUtil(context).exportCSV(data)
        }
    }
}