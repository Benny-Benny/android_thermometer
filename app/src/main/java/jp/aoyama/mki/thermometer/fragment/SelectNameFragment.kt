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
import jp.aoyama.mki.thermometer.viewmodels.TmpViewModel

class SelectNameFragment : Fragment() {
    private val mViewModel: TmpViewModel by viewModels()
    private lateinit var mBinding: SelectNameFragmentBinding
    private lateinit var mAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = SelectNameFragmentBinding.inflate(inflater, container, false)
        mBinding.apply {
            lvList.setOnItemClickListener { _, _, position, _ -> onListItemClick(position) }
        }
        updateList()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding.floatingActionButton.setOnClickListener {
            findNavController().navigate(SelectNameFragmentDirections.selectToEdit())
            super.onViewCreated(view, savedInstanceState)
        }
    }

    private fun updateList() {
        mAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            mViewModel.getUsers(requireContext())
        )
        mBinding.lvList.adapter = mAdapter
    }

    private fun onListItemClick(position: Int) {
        val name = mAdapter.getItem(position).toString()
        findNavController().navigate(SelectNameFragmentDirections.selectToTemperature(name))
    }
}