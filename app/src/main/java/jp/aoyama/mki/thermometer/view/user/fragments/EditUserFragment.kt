package jp.aoyama.mki.thermometer.view.user.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.databinding.FragmentEditUserBinding
import jp.aoyama.mki.thermometer.domain.models.BluetoothData
import jp.aoyama.mki.thermometer.domain.models.Grade
import jp.aoyama.mki.thermometer.view.bluetooth.list.BluetoothListAdapter
import jp.aoyama.mki.thermometer.view.bluetooth.list.BluetoothViewHolder
import jp.aoyama.mki.thermometer.view.user.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class EditUserFragment : Fragment(), BluetoothViewHolder.CallbackListener,
    BluetoothViewHolder.EditCallbackListener {
    private lateinit var mBinding: FragmentEditUserBinding
    private lateinit var mAdapter: BluetoothListAdapter
    private val mViewModel: UserViewModel by viewModels()

    private val args: EditUserFragmentArgs by navArgs()
    private val userId get() = args.userId

    private val spinnerItemClickListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            updateGrade(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEditUserBinding.inflate(inflater, container, false)

        mBinding.apply {
            mAdapter = BluetoothListAdapter(this@EditUserFragment, this@EditUserFragment)
            listBluetoothDevices.layoutManager = LinearLayoutManager(requireContext())
            listBluetoothDevices.adapter = mAdapter
            buttonUpdateName.setOnClickListener { updateName() }
            buttonAddDevice.setOnClickListener {
                findNavController().navigate(
                    EditUserFragmentDirections.editUserToAddBluetooth(userId)
                )
            }

            spinnerGrade.onItemSelectedListener = spinnerItemClickListener
            spinnerGrade.adapter = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
            ).apply {
                val grades = Grade.values().map { it.gradeName }.toMutableList()
                add("選択されていません")
                addAll(grades)

                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }

        reloadData()

        return mBinding.root
    }

    private fun updateName() {
        val input = mBinding.editTextName.text.toString()
        if (input.isBlank()) {
            mBinding.editTextName.error = getString(R.string.message_illegal_input)
            return
        }

        lifecycleScope.launch {
            mViewModel.updateName(userId, input)
            Toast.makeText(requireContext(), "更新しました", Toast.LENGTH_LONG).show()
        }
    }

    override fun onClick(device: BluetoothData) {
        // noop
    }

    override fun onDelete(device: BluetoothData) {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage("この端末を削除しますか")
            .setPositiveButton("削除") { dialog, _ ->
                removeBluetoothDevice(device)
                dialog.dismiss()
            }
            .setNegativeButton("キャンセル") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun removeBluetoothDevice(device: BluetoothData) {
        lifecycleScope.launch {
            mViewModel.removeBluetoothDevice(userId, device.address)
            reloadData()
        }
    }

    private fun updateGrade(position: Int) {
        val grade = when (position) {
            in 1..Grade.values().size -> Grade.values()[position - 1]
            else -> null
        }
        lifecycleScope.launch {
            mViewModel.updateGrade(userId, grade)
        }
    }

    private fun reloadData() {
        lifecycleScope.launch {
            val user = mViewModel.getUser(userId) ?: return@launch
            mBinding.apply {
                editTextName.setText(user.name)
                spinnerGrade.setSelection(
                    if (user.grade != null) user.grade.ordinal + 1 // position=0は何も選択されていない状態
                    else 0
                )
            }
            mAdapter.submitList(user.bluetoothDevices)
        }
    }
}