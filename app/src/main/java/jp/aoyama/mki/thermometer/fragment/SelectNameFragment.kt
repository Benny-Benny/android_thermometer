package jp.aoyama.mki.thermometer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import jp.aoyama.mki.thermometer.databinding.SelectNameFragmentBinding
import jp.aoyama.mki.thermometer.viewmodels.TemperatureViewModel

class SelectNameFragment : Fragment() {
    private val mViewModel: TemperatureViewModel by viewModels()
    private lateinit var mBinding: SelectNameFragmentBinding
    private lateinit var mAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = SelectNameFragmentBinding.inflate(inflater, container, false)
        mBinding.apply {
            lvList.setOnItemClickListener { _, _, position, _ -> onListItemClick(position) }
            mAdapter = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                mutableListOf()
            )
            lvList.adapter = mAdapter
        }

        mViewModel.getUsers(requireContext()).observe(viewLifecycleOwner) { names ->
            mAdapter.clear()
            mAdapter.addAll(names.map { it.name })
        }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding.floatingActionButton.setOnClickListener {
            findNavController().navigate(SelectNameFragmentDirections.selectToEdit())
            super.onViewCreated(view, savedInstanceState)
        }
    }

    private fun onListItemClick(position: Int) {
        val name = mAdapter.getItem(position).toString()
        findNavController().navigate(SelectNameFragmentDirections.selectToTemperature(name))
    }
}