package jp.aoyama.mki.thermometer.view.user.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.databinding.ItemUserBinding
import jp.aoyama.mki.thermometer.view.models.UserEntity

class UserViewHolder(
    private val mBinding: ItemUserBinding,
    private val mCallbackListener: CallbackListener
) :
    RecyclerView.ViewHolder(mBinding.root) {

    interface CallbackListener {
        fun onClick(data: UserEntity)

        fun onEdit(data: UserEntity)
    }

    fun bind(data: UserEntity) {
        mBinding.apply {
            root.setOnClickListener { mCallbackListener.onClick(data) }
            buttonEdit.setOnClickListener { mCallbackListener.onEdit(data) }
            textName.text = data.name

            val statusIcon =
                if (data.found) R.drawable.drawbale_circle_active
                else R.drawable.drawbale_circle_inactive
            imageStatus.setImageResource(statusIcon)
        }
    }

    companion object {
        fun from(parent: ViewGroup, callbackListener: CallbackListener): UserViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemUserBinding.inflate(inflater, parent, false)
            return UserViewHolder(binding, callbackListener)
        }
    }
}