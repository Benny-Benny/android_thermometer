package jp.aoyama.mki.thermometer.fragment.user.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jp.aoyama.mki.thermometer.databinding.ItemUserBinding
import jp.aoyama.mki.thermometer.models.UserEntity

class UserViewHolder(
    private val mBinding: ItemUserBinding,
    private val mCallbackListener: CallbackListener
) :
    RecyclerView.ViewHolder(mBinding.root) {

    interface CallbackListener {
        fun onClick(data: UserEntity)
    }

    fun bind(data: UserEntity) {
        mBinding.root.setOnClickListener { mCallbackListener.onClick(data) }
        mBinding.textName.text = data.name
    }

    companion object {
        fun from(parent: ViewGroup, callbackListener: CallbackListener): UserViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemUserBinding.inflate(inflater, parent, false)
            return UserViewHolder(binding, callbackListener)
        }
    }
}