package jp.aoyama.mki.thermometer.fragment.user.list

import androidx.recyclerview.widget.DiffUtil
import jp.aoyama.mki.thermometer.models.UserEntity

class UserDiffUtil : DiffUtil.ItemCallback<UserEntity>() {
    override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
        return oldItem == newItem
    }
}