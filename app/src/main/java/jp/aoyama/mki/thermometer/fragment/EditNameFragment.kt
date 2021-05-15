package jp.aoyama.mki.thermometer.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.databinding.EditNameFragmentBinding
import jp.aoyama.mki.thermometer.viewmodels.TmpViewModel

class EditNameFragment : Fragment() {

    private val mViewModel: TmpViewModel by viewModels()

    private lateinit var mBinding: EditNameFragmentBinding
    private lateinit var mAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = EditNameFragmentBinding.inflate(inflater, container, false)
        mBinding.apply {
            lvList.setOnItemClickListener { _, _, position, _ -> onItemTouch(position) }
            buttonSave.setOnClickListener { onSaveButtonClick() }
        }
        updateList()
        return mBinding.root
    }

    private fun onSaveButtonClick() {
        val name = mBinding.editTextTextPersonName.text.toString()
        mViewModel.addUser(requireContext(), name)
        mBinding.editTextTextPersonName.setText("")
        updateList()
    }

    private fun updateList() {
        val names = mViewModel.getUsers(requireContext())
        mAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, names)
        mBinding.lvList.adapter = mAdapter

    }

    private fun onItemTouch(position: Int) {
        val name = mAdapter.getItem(position) ?: return

        val builder = AlertDialog.Builder(requireContext()).apply {
            setMessage(R.string.delete_confirm)
            setPositiveButton(R.string.delete_title) { dialog, _ ->
                dialog.dismiss()
                mViewModel.deleteUser(requireContext(), name)
                updateList()
            }
            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }
}
