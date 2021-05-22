package jp.aoyama.mki.thermometer.infrastructure.temperature

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import jp.aoyama.mki.thermometer.domain.models.TemperatureData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class TemperatureCSVRepository {
    suspend fun append(context: Context, data: TemperatureData) = withContext(Dispatchers.IO) {
        val file = getFile(context)
        val outputStream = FileOutputStream(file, true)
        val writer = OutputStreamWriter(outputStream)
        writer.append("${data.toCSV()}\n")
        writer.close()
    }

    private fun getFile(context: Context): File {
        return File(context.getExternalFilesDir(null), FILE_NAME)
    }

    fun getFileUrl(context: Context): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            getFile(context)
        )
    }

    companion object {
        private const val FILE_NAME = "body_temperature.csv"
    }
}