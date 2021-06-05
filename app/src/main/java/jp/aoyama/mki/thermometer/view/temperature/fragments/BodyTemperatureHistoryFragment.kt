package jp.aoyama.mki.thermometer.view.temperature.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.databinding.FragmentBodyTemperatureHistoryBinding
import jp.aoyama.mki.thermometer.view.temperature.list.BodyTemperatureListAdapter
import jp.aoyama.mki.thermometer.view.temperature.viewmodels.TemperatureViewModel
import kotlinx.coroutines.launch

class BodyTemperatureHistoryFragment : Fragment() {
    private lateinit var mBinding: FragmentBodyTemperatureHistoryBinding
    private val mAdapter: BodyTemperatureListAdapter = BodyTemperatureListAdapter()
    private val mViewModel: TemperatureViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentBodyTemperatureHistoryBinding.inflate(layoutInflater, container, false)
        mBinding.apply {
            listBodyTemperature.layoutManager = LinearLayoutManager(requireContext())
            listBodyTemperature.adapter = mAdapter
            swipeRefresh.setOnRefreshListener { reloadData() }
            buttonExport.setOnClickListener { exportData() }
        }

        reloadData()

        return mBinding.root
    }

    private fun reloadData() {
        lifecycleScope.launch {
            val data = mViewModel.getTemperatureData()
            mAdapter.submitList(data)

            mBinding.swipeRefresh.isRefreshing = false
        }
    }

    private fun exportData() {
        lifecycleScope.launch {
            mBinding.progressCircular.visibility = View.VISIBLE

            val fileUri = mViewModel.exportCSV(requireContext())

            mBinding.progressCircular.visibility = View.GONE

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "text/plain"
            }

            startActivity(
                Intent.createChooser(shareIntent, getString(R.string.share_file_description))
            )
        }
    }

    companion object {
        fun newInstance(): BodyTemperatureHistoryFragment {
            return BodyTemperatureHistoryFragment()
        }
    }
}