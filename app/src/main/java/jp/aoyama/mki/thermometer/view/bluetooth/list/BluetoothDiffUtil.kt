package jp.aoyama.mki.thermometer.view.bluetooth.list

import androidx.recyclerview.widget.DiffUtil
import jp.aoyama.mki.thermometer.domain.models.BluetoothData

class BluetoothDiffUtil : DiffUtil.ItemCallback<BluetoothData>() {
    override fun areItemsTheSame(oldItem: BluetoothData, newItem: BluetoothData): Boolean {
        return oldItem.address == newItem.address
    }

    override fun areContentsTheSame(oldItem: BluetoothData, newItem: BluetoothData): Boolean {
        return oldItem == newItem
    }

}