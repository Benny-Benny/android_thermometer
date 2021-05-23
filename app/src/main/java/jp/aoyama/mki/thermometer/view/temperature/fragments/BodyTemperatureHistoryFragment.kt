package jp.aoyama.mki.thermometer.view.temperature.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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
        }

        reloadData()

        return mBinding.root
    }

    private fun reloadData() {
        lifecycleScope.launch {
            val data = mViewModel.getTemperatureData(requireContext())
            mAdapter.submitList(data)

            mBinding.swipeRefresh.isRefreshing = false
        }
    }

    companion object {
        fun newInstance(): BodyTemperatureHistoryFragment {
            return BodyTemperatureHistoryFragment()
        }
    }
}