package jp.aoyama.mki.thermometer.view.temperature.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import jp.aoyama.mki.thermometer.domain.models.TemperatureData
import jp.aoyama.mki.thermometer.infrastructure.temperature.TemperatureCSVRepository

class TemperatureViewModel : ViewModel() {

    private val mCsvFileManager = TemperatureCSVRepository()

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
        mCsvFileManager.append(context, TemperatureData(name = name, temperature = inputValue))

        return true
    }
}