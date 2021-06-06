package jp.aoyama.mki.thermometer.view.temperature.list

import androidx.recyclerview.widget.DiffUtil
import jp.aoyama.mki.thermometer.domain.models.temperature.TemperatureData

class BodyTemperatureDataDiffUtil : DiffUtil.ItemCallback<TemperatureData>() {
    override fun areItemsTheSame(oldItem: TemperatureData, newItem: TemperatureData): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: TemperatureData, newItem: TemperatureData): Boolean {
        return oldItem == newItem
    }
}