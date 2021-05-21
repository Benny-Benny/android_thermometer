package jp.aoyama.mki.thermometer.fragment.bluetooth

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.databinding.ItemBluetoothDeviceBinding

class BluetoothViewHolder(
    private val mBinding: ItemBluetoothDeviceBinding,
    private val callbackListener: CallbackListener
) :
    RecyclerView.ViewHolder(mBinding.root) {

    interface CallbackListener {
        fun onClick(device: BluetoothDevice)
    }

    fun bind(device: BluetoothDevice) {
        val address = device.address ?: return
        mBinding.textDeviceAddress.text = address
        mBinding.textDeviceName.text =
            device.name ?: mBinding.root.context.getString(R.string.unnamed_device)
        mBinding.root.isClickable = true
        mBinding.root.setOnClickListener { callbackListener.onClick(device) }
    }

    companion object {
        fun from(parent: ViewGroup, callbackListener: CallbackListener): BluetoothViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemBluetoothDeviceBinding.inflate(layoutInflater, parent, false)
            return BluetoothViewHolder(binding, callbackListener)
        }

    }
}