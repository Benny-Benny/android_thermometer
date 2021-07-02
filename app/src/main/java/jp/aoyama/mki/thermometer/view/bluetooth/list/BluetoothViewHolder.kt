package jp.aoyama.mki.thermometer.view.bluetooth.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jp.aoyama.mki.thermometer.databinding.ItemBluetoothDeviceBinding
import jp.aoyama.mki.thermometer.domain.models.device.BluetoothScanResult

class BluetoothViewHolder(
    private val mBinding: ItemBluetoothDeviceBinding,
    private val mCallbackListener: CallbackListener,
) :
    RecyclerView.ViewHolder(mBinding.root) {

    interface CallbackListener {
        fun onClick(device: BluetoothScanResult)
    }

    interface EditCallbackListener {
        fun onDelete(device: BluetoothScanResult)
    }

    fun bind(device: BluetoothScanResult) {
        mBinding.apply {
            if (device.name != null) {
                textDeviceName.text = device.name
                textDeviceAddress.text = device.address
            } else {
                textDeviceName.text = device.address
                textDeviceAddress.text = ""
            }
            root.setOnClickListener { mCallbackListener.onClick(device) }
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            callbackListener: CallbackListener
        ): BluetoothViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemBluetoothDeviceBinding.inflate(layoutInflater, parent, false)
            return BluetoothViewHolder(binding, callbackListener)
        }
    }
}