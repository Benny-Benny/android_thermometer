package jp.aoyama.mki.thermometer.view.attendance.list

import androidx.recyclerview.widget.DiffUtil
import jp.aoyama.mki.thermometer.domain.models.attendance.Attendance

class AttendanceDiffUtil : DiffUtil.ItemCallback<Attendance>() {
    override fun areItemsTheSame(oldItem: Attendance, newItem: Attendance): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Attendance, newItem: Attendance): Boolean {
        return oldItem == newItem
    }

}