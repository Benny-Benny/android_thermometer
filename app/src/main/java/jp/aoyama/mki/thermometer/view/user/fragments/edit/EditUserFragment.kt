package jp.aoyama.mki.thermometer.view.user.fragments.edit

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
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
import jp.aoyama.mki.thermometer.domain.models.device.BluetoothScanResult
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.view.bluetooth.list.BluetoothListAdapter
import jp.aoyama.mki.thermometer.view.bluetooth.list.BluetoothViewHolder
import jp.aoyama.mki.thermometer.view.user.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import java.util.*

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

        setHasOptionsMenu(true)
        reloadData()

        return mBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_edit_user, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_user -> deleteUser()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteUser() {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(R.string.delete_confirm)
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                lifecycleScope.launch {
                    mViewModel.deleteUser(requireContext(), userId)
                    findNavController().popBackStack()
                }
            }
            .create()
        dialog.show()
    }

    private fun updateName() {
        val input = mBinding.editTextName.text.toString()
        if (input.isBlank()) {
            mBinding.editTextName.error = getString(R.string.message_illegal_input)
            return
        }

        lifecycleScope.launch {
            mViewModel.updateName(requireContext(), userId, input)
            Toast.makeText(requireContext(), "更新しました", Toast.LENGTH_LONG).show()
        }
    }

    override fun onClick(device: BluetoothScanResult) {
        // noop
    }

    override fun onDelete(device: BluetoothScanResult) {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage("この端末を削除しますか")
            .setPositiveButton("削除") { dialog, _ ->
                removeBluetoothDevice(
                    Device(
                        userId = userId,
                        name = device.name,
                        address = device.address
                    )
                )
                dialog.dismiss()
            }
            .setNegativeButton("キャンセル") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun removeBluetoothDevice(device: Device) {
        lifecycleScope.launch {
            mViewModel.removeBluetoothDevice(requireContext(), device.address)
            reloadData()
        }
    }

    private fun updateGrade(position: Int) {
        val grade = when (position) {
            in 1..Grade.values().size -> Grade.values()[position - 1]
            else -> null
        }
        lifecycleScope.launch {
            mViewModel.updateGrade(requireContext(), userId, grade)
        }
    }

    private fun reloadData() {
        lifecycleScope.launch {
            val user = mViewModel.getUser(requireContext(), userId) ?: return@launch
            mBinding.apply {
                editTextName.setText(user.name)
                spinnerGrade.setSelection(
                    if (user.grade != null) user.grade.ordinal + 1 // position=0は何も選択されていない状態
                    else 0
                )
            }
            mAdapter.submitList(user.devices.map {
                BluetoothScanResult(
                    address = it.address,
                    name = it.name,
                    scannedAt = Calendar.getInstance()
                )
            })
        }
    }
}