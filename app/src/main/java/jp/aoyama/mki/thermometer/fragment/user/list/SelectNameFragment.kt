package jp.aoyama.mki.thermometer.fragment.user.list

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import jp.aoyama.mki.thermometer.databinding.SelectNameFragmentBinding
import jp.aoyama.mki.thermometer.models.UserEntity
import jp.aoyama.mki.thermometer.viewmodels.UserViewModel

class SelectNameFragment : Fragment(), UserViewHolder.CallbackListener {
    private val mViewModel: UserViewModel by viewModels()
    private lateinit var mBinding: SelectNameFragmentBinding
    private val mAdapterNearUser: UserListAdapter = UserListAdapter(this)
    private val mAdapterOutUser: UserListAdapter = UserListAdapter(this)

    private val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val mBluetoothScanCallback: ScanCallback by lazy {
        object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                result?.let { mViewModel.onReceiveBluetoothResult(it) }
            }
        }
    }

    private val mRequestPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            scanBluetoothDevices()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = SelectNameFragmentBinding.inflate(inflater, container, false)
        mBinding.apply {
            listNearUser.layoutManager = LinearLayoutManager(requireContext())
            listNearUser.adapter = mAdapterNearUser
            (listNearUser.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            listOutUser.layoutManager = LinearLayoutManager(requireContext())
            listOutUser.adapter = mAdapterOutUser

            floatingActionButton.setOnClickListener {
                findNavController().navigate(SelectNameFragmentDirections.selectToEdit())
            }
        }

        mViewModel.getUsers(requireContext()).observe(viewLifecycleOwner) { data ->
            mAdapterNearUser.submitList(data.near)
            mAdapterOutUser.submitList(data.outs)
        }

        val accessFileLocation = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (accessFileLocation != PackageManager.PERMISSION_GRANTED) scanBluetoothDevices()
        else mRequestPermission.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))

        return mBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBluetoothAdapter.isDiscovering) mBluetoothAdapter.cancelDiscovery()
        mBluetoothAdapter.bluetoothLeScanner.stopScan(mBluetoothScanCallback)
    }

    private fun scanBluetoothDevices() {
        if (mBluetoothAdapter.isDiscovering) mBluetoothAdapter.cancelDiscovery()
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            .build()
        mBluetoothAdapter.bluetoothLeScanner.startScan(null, scanSettings, mBluetoothScanCallback)
    }

    // ============================
    // UserViewHolder.CallbackListener
    // ============================
    override fun onClick(data: UserEntity) {
        findNavController().navigate(SelectNameFragmentDirections.selectToTemperature(data.name))
    }
}