package jp.aoyama.mki.thermometer.view.temperature.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.databinding.ItemBodyTemperatureBinding
import jp.aoyama.mki.thermometer.domain.models.temperature.TemperatureData
import java.util.*

class BodyTemperatureViewHolder(
    private val mBinding: ItemBodyTemperatureBinding
) : RecyclerView.ViewHolder(mBinding.root) {

    fun bind(data: TemperatureData) {
        mBinding.apply {
            textUserName.text = data.name
            val context = mBinding.root.context
            textTemperature.text = context.getString(
                R.string.format_body_temperature,
                data.temperature
            )
            textMeasuredAt.text = context.getString(
                R.string.format_ymdhm,
                data.createdAt.get(Calendar.YEAR),
                data.createdAt.get(Calendar.MONTH),
                data.createdAt.get(Calendar.DAY_OF_MONTH),
                data.createdAt.get(Calendar.HOUR_OF_DAY),
                data.createdAt.get(Calendar.MINUTE)
            )
        }
    }

    companion object {
        fun create(parent: ViewGroup): BodyTemperatureViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemBodyTemperatureBinding.inflate(inflater, parent, false)
            return BodyTemperatureViewHolder(binding)
        }
    }
}