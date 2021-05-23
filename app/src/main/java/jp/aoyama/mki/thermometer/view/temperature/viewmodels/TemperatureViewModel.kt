package jp.aoyama.mki.thermometer.view.temperature.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import jp.aoyama.mki.thermometer.domain.models.TemperatureData
import jp.aoyama.mki.thermometer.domain.repository.TemperatureRepository
import jp.aoyama.mki.thermometer.infrastructure.temperature.TemperatureCSVRepository

class TemperatureViewModel : ViewModel() {

    suspend fun getTemperatureData(context: Context): List<TemperatureData> {
        val repository: TemperatureRepository = LocalFileTemperatureRepository(context)
        return withContext(Dispatchers.IO) {
            repository.findAll().sortedBy { it.createdAt.timeInMillis }.reversed()
        }
    }

    /**
     * 入力された体温をCSV形式で保存
     * @param name 体温を記録する人の名前
     * @param temperature 体温
     * @return 入力値が適切であれば true を返す
     */
    suspend fun saveTemperature(context: Context, name: String, temperature: String?): Boolean {
        val inputValue = temperature?.toFloatOrNull() ?: return false
        if (inputValue > 45f || inputValue < 35f) return false

        // 内部のＣＳＶファイルにデータを追加
        val data = TemperatureData(name = name, temperature = inputValue)
        val repository: TemperatureRepository = TemperatureCSVRepository(context)
        repository.add(data)

        return true
    }
}