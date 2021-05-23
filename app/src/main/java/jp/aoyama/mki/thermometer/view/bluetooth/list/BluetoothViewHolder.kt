package jp.aoyama.mki.thermometer.view.bluetooth.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.databinding.ItemBluetoothDeviceBinding
import jp.aoyama.mki.thermometer.domain.models.BluetoothData

class BluetoothViewHolder(
    private val mBinding: ItemBluetoothDeviceBinding,
    private val mCallbackListener: CallbackListener,
    private val mEditCallbackListener: EditCallbackListener? = null,
) :
    RecyclerView.ViewHolder(mBinding.root) {

    interface CallbackListener {
        fun onClick(device: BluetoothData)
    }

    interface EditCallbackListener {
        fun onDelete(device: BluetoothData)
    }

    fun bind(device: BluetoothData) {
        mBinding.apply {
            textDeviceAddress.text = device.address
            textDeviceName.text =
                device.name ?: mBinding.root.context.getString(R.string.unnamed_device)
            root.setOnClickListener { mCallbackListener.onClick(device) }
        }

        if (mEditCallbackListener != null) {
            mBinding.buttonDelete.apply {
                visibility = View.VISIBLE
                setOnClickListener { mEditCallbackListener.onDelete(device) }
            }
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            callbackListener: CallbackListener,
            editCallbackListener: EditCallbackListener? = null
        ): BluetoothViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemBluetoothDeviceBinding.inflate(layoutInflater, parent, false)
            return BluetoothViewHolder(binding, callbackListener, editCallbackListener)
        }
    }
}