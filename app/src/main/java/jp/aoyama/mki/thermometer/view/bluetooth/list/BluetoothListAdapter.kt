package jp.aoyama.mki.thermometer.view.bluetooth.list

import android.bluetooth.BluetoothDevice
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

class BluetoothListAdapter(private val callbackListener: BluetoothViewHolder.CallbackListener) :
    ListAdapter<BluetoothDevice, BluetoothViewHolder>(BluetoothDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothViewHolder {
        return BluetoothViewHolder.from(parent, callbackListener)
    }

    override fun onBindViewHolder(holder: BluetoothViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}