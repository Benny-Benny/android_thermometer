package jp.aoyama.mki.thermometer.fragment.user.create

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import jp.aoyama.mki.thermometer.databinding.SelectBluetoothDeviceFragmentBinding
import jp.aoyama.mki.thermometer.fragment.bluetooth.BluetoothListAdapter
import jp.aoyama.mki.thermometer.fragment.bluetooth.BluetoothViewHolder
import jp.aoyama.mki.thermometer.viewmodels.CreateUserSharedViewModel


class SelectBluetoothDeviceFragment : BluetoothViewHolder.CallbackListener, Fragment() {

    private lateinit var mBinding: SelectBluetoothDeviceFragmentBinding
    private val mAdapter: BluetoothListAdapter = BluetoothListAdapter(this)
    private val mViewModel: CreateUserSharedViewModel by viewModels({ requireActivity() })

    private val mRequestPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            findPairedDevices()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = SelectBluetoothDeviceFragmentBinding.inflate(inflater, container, false)

        mBinding.apply {
            listDevices.adapter = mAdapter
            listDevices.layoutManager = LinearLayoutManager(requireContext())
            buttonBack.setOnClickListener { findNavController().popBackStack() }
        }

        val accessFileLocation = checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (accessFileLocation != PackageManager.PERMISSION_GRANTED) findPairedDevices()
        else mRequestPermission.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))


        return mBinding.root
    }

    private fun findPairedDevices() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return
        mAdapter.submitList(bluetoothAdapter.bondedDevices.toList())
    }


    // =====================================
    // BluetoothViewHolder.CallbackListener
    // =====================================

    override fun onClick(device: BluetoothDevice) {
        mViewModel.bluetoothDeviceName = device.name
        mViewModel.bluetoothMacAddress = device.address
        findNavController().navigate(SelectBluetoothDeviceFragmentDirections.selectBluetoothToConfirm())
    }

    companion object {
        private const val TAG = "SelectBluetoothDeviceFr"
    }
}