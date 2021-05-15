package jp.aoyama.mki.thermometer.models

import java.text.SimpleDateFormat
import java.util.*

data class TemperatureData(
    val temperature: Float,
    val name: String,
    val createdAt: Calendar = Calendar.getInstance()
){
    fun toCSV(): String {
        val dateFormat = SimpleDateFormat("MM/dd")
        return "${dateFormat.format(createdAt.time)}, ${name}, ${temperature}"
    }

}