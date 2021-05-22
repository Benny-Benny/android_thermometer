package jp.aoyama.mki.thermometer.view.user.list

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import jp.aoyama.mki.thermometer.view.models.UserEntity

class UserListAdapter(private val mCallbackListener: UserViewHolder.CallbackListener) :
    ListAdapter<UserEntity, UserViewHolder>(UserDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder.from(parent, mCallbackListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}