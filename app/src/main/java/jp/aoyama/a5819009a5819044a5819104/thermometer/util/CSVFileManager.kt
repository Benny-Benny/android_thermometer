package jp.aoyama.a5819009a5819044a5819104.thermometer.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import jp.aoyama.a5819009a5819044a5819104.thermometer.models.TemperatureData
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class CSVFileManager {
    suspend fun append(context: Context, data: TemperatureData) {
        val file = getFile(context)
        val outputStream = FileOutputStream(file, true)
        val writer = OutputStreamWriter(outputStream)
        writer.append("${data.toCSV()}\n")
        writer.close()
    }

    fun getFile(context: Context): File {
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