package jp.aoyama.mki.thermometer.view.temperature.list

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import jp.aoyama.mki.thermometer.domain.models.temperature.TemperatureData

class BodyTemperatureListAdapter :
    ListAdapter<TemperatureData, BodyTemperatureViewHolder>(BodyTemperatureDataDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BodyTemperatureViewHolder {
        return BodyTemperatureViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: BodyTemperatureViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}