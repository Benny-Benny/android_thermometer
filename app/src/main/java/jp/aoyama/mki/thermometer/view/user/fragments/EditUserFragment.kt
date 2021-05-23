package jp.aoyama.mki.thermometer.view.user.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        }

        reloadBluetoothList()

        return mBinding.root
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
            mViewModel.removeBluetoothDevice(requireContext(), userId, device.address)
            reloadBluetoothList()
        }
    }

    private fun reloadBluetoothList() {
        lifecycleScope.launch {
            val user = mViewModel.getUser(requireContext(), userId) ?: return@launch
            mBinding.editTextName.setText(user.name)
            mAdapter.submitList(user.bluetoothDevices)
        }
    }
}