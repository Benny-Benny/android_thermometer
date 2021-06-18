package jp.aoyama.mki.thermometer.infrastructure.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.domain.models.temperature.TemperatureData
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

class TemperatureCSVUtil(private val mContext: Context) {

    private val temperatureDataFile
        get() = File(
            mContext.getExternalFilesDir(null),
            mContext.getString(R.string.export_file_name_body_temperature)
        )

    /**
     * 体温データをCSVファイルに出力
     */
    fun exportCSV(data: List<TemperatureData>): Uri {
        val outputStream = FileOutputStream(temperatureDataFile, false)

        val sortByDate = data.sortedBy { it.createdAt.timeInMillis }

        OutputStreamWriter(outputStream).use { writer ->
            sortByDate.forEach { writer.append("${it.toCSV()}\n") }
        }

        return FileProvider.getUriForFile(
            mContext,
            "${mContext.packageName}.provider",
            temperatureDataFile
        )
    }


    private fun TemperatureData.toCSV(): String {
        val formatter = SimpleDateFormat("MM/dd", Locale.JAPAN)
        val date = formatter.format(createdAt.time)
        val data = arrayOf(date, name, temperature)
        return data.joinToString()
    }

}