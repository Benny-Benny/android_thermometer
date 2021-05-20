package jp.aoyama.mki.thermometer.fragment.bluetooth

import android.bluetooth.BluetoothDevice
import androidx.recyclerview.widget.DiffUtil

class BluetoothDiffUtil : DiffUtil.ItemCallback<BluetoothDevice>() {
    override fun areItemsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean {
        return oldItem.address == newItem.address
    }

    override fun areContentsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean {
        return oldItem.address == newItem.address
    }
}