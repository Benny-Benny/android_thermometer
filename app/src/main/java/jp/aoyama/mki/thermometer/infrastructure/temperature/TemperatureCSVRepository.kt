package jp.aoyama.mki.thermometer.infrastructure.temperature

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import jp.aoyama.mki.thermometer.domain.models.TemperatureData
import jp.aoyama.mki.thermometer.domain.repository.TemperatureRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class TemperatureCSVRepository(
    private val mContext: Context
) : TemperatureRepository {

    private val temperatureDataFile get() = File(mContext.getExternalFilesDir(null), FILE_NAME)

    override suspend fun add(data: TemperatureData): Unit = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            val outputStream = FileOutputStream(temperatureDataFile, true)
            val writer = OutputStreamWriter(outputStream)
            writer.append("${data.toCSV()}\n")
            writer.close()
        }.onFailure { e ->
            throw e
        }
    }

    fun getFileUrl(context: Context): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            temperatureDataFile
        )
    }

    companion object {
        private const val FILE_NAME = "body_temperature.csv"
    }
}