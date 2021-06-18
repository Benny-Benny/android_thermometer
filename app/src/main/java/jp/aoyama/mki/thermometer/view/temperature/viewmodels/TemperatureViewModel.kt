package jp.aoyama.mki.thermometer.view.temperature.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import jp.aoyama.mki.thermometer.domain.models.temperature.TemperatureData
import jp.aoyama.mki.thermometer.domain.service.UserService
import jp.aoyama.mki.thermometer.infrastructure.export.TemperatureCSVUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TemperatureViewModel : ViewModel() {
    suspend fun getTemperatureData(context: Context): List<TemperatureData> {
        val service = UserService(context)
        return kotlin.runCatching {
            service.getTemperatureData()
        }.fold(
            onSuccess = { it },
            onFailure = { e ->
                Log.e(TAG, "getTemperatureData: error while getting temperature data", e)
                emptyList()
            }
        )
    }

    suspend fun saveTemperature(context: Context, userId: String, temperature: Float?): Boolean {
        val service = UserService(context)
        return kotlin.runCatching {
            service.saveTemperature(userId, temperature)
        }.fold(
            onSuccess = { it },
            onFailure = { e ->
                Log.e(TAG, "saveTemperature: error while saving temperature", e)
                Toast.makeText(context, "エラーが発生しました", Toast.LENGTH_LONG).show()
                false
            }
        )
    }

    suspend fun exportCSV(context: Context): Uri {
        val data = getTemperatureData(context)
        return withContext(Dispatchers.IO) {
            TemperatureCSVUtil(context).exportCSV(data)
        }
    }

    companion object {
        private const val TAG = "TemperatureViewModel"
    }
}