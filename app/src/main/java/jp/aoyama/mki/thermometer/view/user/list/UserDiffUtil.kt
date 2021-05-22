package jp.aoyama.mki.thermometer.view.user.list

import androidx.recyclerview.widget.DiffUtil
import jp.aoyama.mki.thermometer.view.models.UserEntity

class UserDiffUtil : DiffUtil.ItemCallback<UserEntity>() {
    override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
        return oldItem == newItem
    }
}