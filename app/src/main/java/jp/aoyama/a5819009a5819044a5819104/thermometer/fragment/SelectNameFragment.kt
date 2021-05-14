package jp.aoyama.a5819009a5819044a5819104.thermometer.fragment

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.gson.Gson
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import jp.aoyama.a5819009a5819044a5819104.thermometer.databinding.SelectNameFragmentBinding
import jp.aoyama.a5819009a5819044a5819104.thermometer.util.CSVFileManager
import jp.aoyama.a5819009a5819044a5819104.thermometer.viewmodels.TmpViewModel

class SelectNameFragment : Fragment() {


    private var mNameList: List<String> = mutableListOf()
    private lateinit var mAdapter: ArrayAdapter<Any>
    private val mCsvFileManager = CSVFileManager()

    companion object {
        fun newInstance() = SelectNameFragment()
        private const val REQUEST_EDIT = 0x0050
    }

    private val viewModel: TmpViewModel by viewModels()
    private lateinit var binding: SelectNameFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SelectNameFragmentBinding.inflate(inflater, container, false)
        mNameList = viewModel.getUsers(requireContext())
        updateList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(SelectNameFragmentDirections.selectToEdit())
            super.onViewCreated(view, savedInstanceState)
        }
    }


    fun updateList() {
        mAdapter =
            ArrayAdapter<Any>(requireContext(), android.R.layout.simple_list_item_1, mNameList)
        val lvList = binding.lvList
        lvList.adapter = mAdapter
        lvList.onItemClickListener = ListItemClickListener()
    }


    private inner class ListItemClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            val name = parent.getItemAtPosition(position).toString()
            findNavController().navigate(SelectNameFragmentDirections.selectToTemperature(name))
        }
    }
}