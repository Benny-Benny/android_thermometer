package jp.aoyama.mki.thermometer.view.temperature.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel

class MeasureBodyTemperatureViewModel : ViewModel() {
    private var inputValues = mutableListOf<Float>()

    /**
     * 画像から取得された値から、数字を抽出
     */
    fun addData(value: String) {
        Log.d("VIEWMODEL", "addData: ${value}")
        val numStr = value.replace("[^0-9]".toRegex(), "")
        if (numStr.isBlank() || numStr.length > 6) return
        Log.d("temp", numStr)
        var num = numStr.toInt()
        if (num in 310..319) {
            num += 60
        }
        when (num) {
            36 -> num = 361
            37 -> num = 371
            31 -> num = 371
        }
        if (num in 350..450) {
            inputValues.add(num.toFloat() / 10F)
        }


    }

    /**
     * 入力された値から、頻出な値を取得
     */
    fun getData(): Float? {
        // 最新の15個のデータから, 値:出現回数 となるようにマップを取得
        val frequencyMap = inputValues.takeLast(15).groupingBy { it }.eachCount()

        // 出現回数が最大で、かつ3回以上出現した値を取得
        val max = frequencyMap.maxByOrNull { it.value } ?: return null
        if (max.value < 3F) return null

        return max.key
    }
}