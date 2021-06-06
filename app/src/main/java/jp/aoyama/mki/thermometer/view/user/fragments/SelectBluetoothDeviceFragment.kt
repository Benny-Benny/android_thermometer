package jp.aoyama.mki.thermometer.view.user.fragments

import android.Manifest
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
import jp.aoyama.mki.thermometer.databinding.FragmentBluetoothPairingBinding
import jp.aoyama.mki.thermometer.domain.models.device.BluetoothScanResult
import jp.aoyama.mki.thermometer.domain.repository.BluetoothDeviceScanner
import jp.aoyama.mki.thermometer.infrastructure.android.bluetooth.BluetoothDiscoveryDeviceScanner
import jp.aoyama.mki.thermometer.view.bluetooth.list.BluetoothListAdapter
import jp.aoyama.mki.thermometer.view.bluetooth.list.BluetoothViewHolder
import jp.aoyama.mki.thermometer.view.user.viewmodels.CreateUserSharedViewModel

class SelectBluetoothDeviceFragment : Fragment(), BluetoothViewHolder.CallbackListener {

    private lateinit var mBinding: FragmentBluetoothPairingBinding
    private lateinit var mBluetoothDeviceScanner: BluetoothDeviceScanner
    private val mAdapter: BluetoothListAdapter = BluetoothListAdapter(this)
    private val mViewModel: CreateUserSharedViewModel by viewModels({ requireActivity() })

    private val mRequestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) findBluetoothDevices()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBluetoothDeviceScanner = BluetoothDiscoveryDeviceScanner(requireContext())
        mBinding = FragmentBluetoothPairingBinding.inflate(inflater, container, false)

        mBinding.apply {
            buttonSkip.setOnClickListener {
                findNavController().navigate(SelectBluetoothDeviceFragmentDirections.pairingToConfirm())
            }
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }

            listBluetoothDevices.adapter = mAdapter
            listBluetoothDevices.layoutManager = LinearLayoutManager(requireContext())
        }

        mBluetoothDeviceScanner.devicesLiveData.observe(viewLifecycleOwner) { devices ->
            val foundDevices = devices
                .filter { it.name != null }

            mBinding.progressCircular.visibility =
                if (foundDevices.isEmpty()) View.VISIBLE
                else View.GONE

            mAdapter.submitList(foundDevices)
        }

        val accessFineLocation = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (accessFineLocation == PackageManager.PERMISSION_GRANTED) findBluetoothDevices()
        else mRequestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        return mBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        mBluetoothDeviceScanner.cancelDiscovery()
    }

    private fun findBluetoothDevices() {
        mBluetoothDeviceScanner.startDiscovery()
    }

    // =====================================
    // BluetoothViewHolder.CallbackListener
    // =====================================
    override fun onClick(device: BluetoothScanResult) {
        mViewModel.bluetoothDeviceName = device.name
        mViewModel.bluetoothMacAddress = device.address
        findNavController().navigate(SelectBluetoothDeviceFragmentDirections.pairingToConfirm())
    }
}