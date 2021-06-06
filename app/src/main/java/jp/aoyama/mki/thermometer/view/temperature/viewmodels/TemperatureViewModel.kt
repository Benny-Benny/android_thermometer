package jp.aoyama.mki.thermometer.view.temperature.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import jp.aoyama.mki.thermometer.domain.models.temperature.TemperatureData
import jp.aoyama.mki.thermometer.domain.service.UserService
import jp.aoyama.mki.thermometer.infrastructure.local.user.TemperatureCSVUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TemperatureViewModel : ViewModel() {
    suspend fun getTemperatureData(context: Context): List<TemperatureData> {
        val service = UserService(context)
        return service.getTemperatureData()
    }

    suspend fun saveTemperature(context: Context, userId: String, temperature: Float?): Boolean {
        val service = UserService(context)
        return service.saveTemperature(userId, temperature)
    }

    suspend fun exportCSV(context: Context): Uri {
        val data = getTemperatureData(context)
        return withContext(Dispatchers.IO) {
            TemperatureCSVUtil(context).exportCSV(data)
        }
    }
}