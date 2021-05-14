package jp.aoyama.a5819009a5819044a5819104.thermometer.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import jp.aoyama.a5819009a5819044a5819104.thermometer.R
import jp.aoyama.a5819009a5819044a5819104.thermometer.databinding.EditNameFragmentBinding
import jp.aoyama.a5819009a5819044a5819104.thermometer.viewmodels.TmpViewModel

class EditNameFragment : Fragment() {

    companion object {
        private const val TAG = "editNameActivity"
    }

    private val viewModel: TmpViewModel by viewModels()

    private lateinit var binding: EditNameFragmentBinding
    private lateinit var mAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EditNameFragmentBinding.inflate(inflater, container, false)
        updateList()
        binding.buttonSave.setOnClickListener { onSaveButtonClick() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: HELLo")
    }

    fun onSaveButtonClick() {
        val name = binding.editTextTextPersonName.text.toString()
        viewModel.addUser(requireContext(), name)
        binding.editTextTextPersonName.setText("")
        updateList()
    }

    fun updateList() {
        val names = viewModel.getUsers(requireContext())
        mAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, names)
        val lvList = binding.lvList
        lvList.adapter = mAdapter
        lvList.onItemClickListener = ListItemClickListener()

    }

    private inner class ListItemClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            val name = mAdapter.getItem(position) ?: return
            val builder = AlertDialog.Builder(requireContext()).apply {
                setMessage(R.string.delete_confirm)
                setPositiveButton(R.string.delete_title) { dialog, id ->
                    dialog.dismiss()
                    viewModel.deleteUser(requireContext(), name)
                    updateList()
                }
                setNegativeButton(R.string.cancel) { dialog, id ->
                    dialog.dismiss()
                }
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
}
