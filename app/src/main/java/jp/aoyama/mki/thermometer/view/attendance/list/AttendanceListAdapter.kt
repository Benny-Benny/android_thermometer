package jp.aoyama.mki.thermometer.view.attendance.list

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import jp.aoyama.mki.thermometer.domain.models.attendance.Attendance

class AttendanceListAdapter :
    ListAdapter<Attendance, AttendanceListViewHolder>(AttendanceDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceListViewHolder {
        return AttendanceListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AttendanceListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}