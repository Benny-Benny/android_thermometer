package jp.aoyama.mki.thermometer.infrastructure.csv.user

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import jp.aoyama.mki.thermometer.domain.models.TemperatureData
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

class TemperatureCSVUtil(private val mContext: Context) {

    private val temperatureDataFile get() = File(mContext.getExternalFilesDir(null), FILE_NAME)

    suspend fun exportCSV(data: List<TemperatureData>): Uri {
        val outputStream = FileOutputStream(temperatureDataFile, false)
        val writer = OutputStreamWriter(outputStream)

        val sortByDate = data.sortedBy { it.createdAt.timeInMillis }
        sortByDate.forEach { writer.append("${it.toCSV()}\n") }
        writer.close()
        return getFileUrl(context = mContext)
    }

    private fun getFileUrl(context: Context): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            temperatureDataFile
        )
    }

    private fun TemperatureData.toCSV(): String {
        val dateFormat = SimpleDateFormat("MM/dd", Locale.JAPAN)
        return "${dateFormat.format(createdAt.time)}, $name, $temperature"
    }

    companion object {
        private const val FILE_NAME = "body_temperature.csv"
    }
}