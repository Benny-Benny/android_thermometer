package jp.aoyama.mki.thermometer.view.attendance.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jp.aoyama.mki.thermometer.databinding.ItemAttendanceBinding
import jp.aoyama.mki.thermometer.domain.models.attendance.Attendance
import java.text.SimpleDateFormat
import java.util.*

class AttendanceListViewHolder(private val mBinding: ItemAttendanceBinding) :
    RecyclerView.ViewHolder(mBinding.root) {

    fun bind(attendance: Attendance) {
        mBinding.textName.text = attendance.userName

        val formatter = SimpleDateFormat("MM/dd HH:mm", Locale.JAPAN)
        val enterStr = formatter.format(attendance.enterAt.timeInMillis)
        val leftStr =
            if (attendance.leftAt != null) formatter.format(attendance.leftAt.time) else ""
        mBinding.textEnterAndLeft.text = "$enterStr - $leftStr"
    }

    companion object {
        fun from(parent: ViewGroup): AttendanceListViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemAttendanceBinding.inflate(inflater, parent, false)
            return AttendanceListViewHolder(binding)
        }
    }
}