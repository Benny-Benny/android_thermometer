package jp.aoyama.mki.thermometer.view.temperature.viewmodels

import androidx.lifecycle.ViewModel

class MeasureBodyTemperatureViewModel : ViewModel() {
    private var inputValues = mutableListOf<Float>()

    /**
     * 画像から取得された値から、数字を抽出
     */
    fun addData(value: String) {
        val numStr = value.replace("[^0-9]".toRegex(), "")
        if (numStr.isBlank() || numStr.length > 6) return

        val num = when (val num = numStr.toInt()) {
            in 350..420 -> num.toFloat() / 10F
            31 -> 37.1f
            36 -> 36.1f
            else -> return
        }

        inputValues.add(num)
    }

    /**
     * 入力された値から、頻出な値を取得
     */
    fun getData(): Float? {
        // 最新の10個のデータから, 値:出現回数 となるようにマップを取得
        val frequencyMap = inputValues.takeLast(10).groupingBy { it }.eachCount()

        // 出現回数が最大で、かつ5回以上出現した値を取得
        val max = frequencyMap.maxByOrNull { it.value } ?: return null
        if (max.value < 5F) return null

        return max.key
    }
}