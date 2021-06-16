package jp.aoyama.mki.thermometer.view.bluetooth.list

import androidx.recyclerview.widget.DiffUtil
import jp.aoyama.mki.thermometer.domain.models.device.BluetoothScanResult

class BluetoothDiffUtil : DiffUtil.ItemCallback<BluetoothScanResult>() {
    override fun areItemsTheSame(
        oldItem: BluetoothScanResult,
        newItem: BluetoothScanResult
    ): Boolean {
        return oldItem.address == newItem.address
    }

    override fun areContentsTheSame(
        oldItem: BluetoothScanResult,
        newItem: BluetoothScanResult
    ): Boolean {
        return oldItem == newItem
    }

}