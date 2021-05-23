package jp.aoyama.mki.thermometer.view.bluetooth.list

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import jp.aoyama.mki.thermometer.domain.models.BluetoothData

class BluetoothListAdapter(
    private val callbackListener: BluetoothViewHolder.CallbackListener,
    private val editCallbackListener: BluetoothViewHolder.EditCallbackListener? = null
) :
    ListAdapter<BluetoothData, BluetoothViewHolder>(BluetoothDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothViewHolder {
        return BluetoothViewHolder.from(parent, callbackListener, editCallbackListener)
    }

    override fun onBindViewHolder(holder: BluetoothViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}