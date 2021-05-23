package jp.aoyama.mki.thermometer.view.temperature.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import jp.aoyama.mki.thermometer.domain.models.TemperatureData
import jp.aoyama.mki.thermometer.domain.repository.TemperatureRepository
import jp.aoyama.mki.thermometer.infrastructure.temperature.LocalFileTemperatureRepository
import jp.aoyama.mki.thermometer.infrastructure.temperature.TemperatureCSVUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TemperatureViewModel : ViewModel() {

    suspend fun getTemperatureData(context: Context): List<TemperatureData> {
        val repository: TemperatureRepository = LocalFileTemperatureRepository(context)
        return withContext(Dispatchers.IO) {
            repository.findAll().sortedBy { it.createdAt.timeInMillis }.reversed()
        }
    }

    /**
     * 入力された体温を保存
     * @param name 体温を記録する人の名前
     * @param temperature 体温
     * @return 入力値が適切であれば true を返す
     */
    suspend fun saveTemperature(context: Context, name: String, temperature: Float?): Boolean {
        if (temperature == null) return false
        if (temperature > 45f || temperature < 35f) return false

        val data = TemperatureData(name = name, temperature = temperature)
        val repository: TemperatureRepository = LocalFileTemperatureRepository(context)
        repository.add(data)

        return true
    }

    suspend fun exportCSV(context: Context): Uri {
        val data = getTemperatureData(context)
        return withContext(Dispatchers.IO) {
            TemperatureCSVUtil(context).exportCSV(data)
        }
    }
}