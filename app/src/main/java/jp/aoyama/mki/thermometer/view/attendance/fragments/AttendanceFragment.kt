package jp.aoyama.mki.thermometer.view.attendance.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import jp.aoyama.mki.thermometer.databinding.FragmentAttendanceBinding
import jp.aoyama.mki.thermometer.infrastructure.workmanager.ExportAttendanceWorker
import jp.aoyama.mki.thermometer.view.attendance.list.AttendanceListAdapter
import jp.aoyama.mki.thermometer.view.attendance.viewmodels.AttendanceViewModel
import kotlinx.coroutines.launch

class AttendanceFragment : Fragment() {

    private lateinit var mBinding: FragmentAttendanceBinding
    private val mAttendanceListAdapter: AttendanceListAdapter = AttendanceListAdapter()
    private val mViewModel: AttendanceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAttendanceBinding.inflate(inflater, container, false)
        mBinding.apply {
            recyclerViewAttendance.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewAttendance.adapter = mAttendanceListAdapter
            swipeRefresh.setOnRefreshListener { reloadData() }
            buttonExport.setOnClickListener {
                ExportAttendanceWorker.startWork(requireContext().applicationContext)
            }
        }

        reloadData()

        return mBinding.root
    }

    private fun reloadData() {
        lifecycleScope.launch {
            mBinding.progressCircular.visibility = View.VISIBLE
            val attendances = mViewModel.getAttendances(requireContext())
            Log.d(TAG, "reloadData: $attendances")
            mBinding.progressCircular.visibility = View.GONE

            mAttendanceListAdapter.submitList(attendances)
            mBinding.swipeRefresh.isRefreshing = false
        }
    }

    companion object {
        private const val TAG = "AttendanceFragment"
        fun newInstance(): AttendanceFragment {
            return AttendanceFragment()
        }
    }
}