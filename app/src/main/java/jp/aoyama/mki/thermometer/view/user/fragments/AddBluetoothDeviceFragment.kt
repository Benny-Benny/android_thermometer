package jp.aoyama.mki.thermometer.view.user.fragments

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
import jp.aoyama.mki.thermometer.databinding.FeagmentAddBluetoothDeviceBinding
import jp.aoyama.mki.thermometer.domain.models.BluetoothData
import jp.aoyama.mki.thermometer.view.bluetooth.list.BluetoothListAdapter
import jp.aoyama.mki.thermometer.view.bluetooth.list.BluetoothViewHolder
import jp.aoyama.mki.thermometer.view.bluetooth.scanner.BluetoothDeviceScanner
import jp.aoyama.mki.thermometer.view.bluetooth.scanner.BluetoothDiscoveryDeviceScanner
import jp.aoyama.mki.thermometer.view.user.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class AddBluetoothDeviceFragment : Fragment(), BluetoothViewHolder.CallbackListener {

    private val mViewModel: UserViewModel by viewModels()
    private lateinit var mBinding: FeagmentAddBluetoothDeviceBinding
    private lateinit var mAdapter: BluetoothListAdapter
    private lateinit var mBluetoothDeviceScanner: BluetoothDeviceScanner

    private val args: AddBluetoothDeviceFragmentArgs by navArgs()
    private val userId get() = args.userId
    private val mUserDevices: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FeagmentAddBluetoothDeviceBinding.inflate(layoutInflater, container, false)

        mBinding.apply {
            mAdapter = BluetoothListAdapter(this@AddBluetoothDeviceFragment)
            listBluetoothDevices.layoutManager = LinearLayoutManager(requireContext())
            listBluetoothDevices.adapter = mAdapter
        }

        lifecycleScope.launch {
            val user = mViewModel.getUser(requireContext(), userId) ?: return@launch
            val addresses = user.bluetoothDevices.map { it.address }
            mUserDevices.addAll(addresses)
        }

        mBluetoothDeviceScanner = BluetoothDiscoveryDeviceScanner(requireContext())
        mBluetoothDeviceScanner.startDiscovery()
        mBluetoothDeviceScanner.devicesLiveData.observe(viewLifecycleOwner) { devices ->
            // 登録済みのデバイスを一覧から削除
            val notRegistered = devices.toMutableList()
            notRegistered.removeAll { mUserDevices.contains(it.device.address) }

            mAdapter.submitList(notRegistered.map {
                BluetoothData(
                    name = it.device.name,
                    address = it.device.address
                )
            })
        }
        return mBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        mBluetoothDeviceScanner.cancelDiscovery()
    }

    override fun onClick(device: BluetoothData) {
        lifecycleScope.launch {
            mViewModel.addBluetoothDevice(requireContext(), userId, device)
            findNavController().popBackStack()
            Toast.makeText(requireContext(), "端末を追加しました", Toast.LENGTH_LONG).show()
        }
    }
}