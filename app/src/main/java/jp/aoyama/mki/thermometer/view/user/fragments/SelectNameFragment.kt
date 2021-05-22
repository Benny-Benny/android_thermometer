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
import androidx.recyclerview.widget.SimpleItemAnimator
import jp.aoyama.mki.thermometer.databinding.SelectNameFragmentBinding
import jp.aoyama.mki.thermometer.view.bluetooth.BluetoothScanController
import jp.aoyama.mki.thermometer.view.models.UserEntity
import jp.aoyama.mki.thermometer.view.user.list.UserListAdapter
import jp.aoyama.mki.thermometer.view.user.list.UserViewHolder
import jp.aoyama.mki.thermometer.view.user.viewmodels.UserViewModel

class SelectNameFragment : Fragment(), UserViewHolder.CallbackListener {
    private val mViewModel: UserViewModel by viewModels()
    private lateinit var mBinding: SelectNameFragmentBinding
    private val mAdapterNearUser: UserListAdapter = UserListAdapter(this)
    private val mAdapterOutUser: UserListAdapter = UserListAdapter(this)
    private val mBluetoothScanController: BluetoothScanController by lazy {
        BluetoothScanController(requireContext(), timeoutInMillis = 30 * 1000)
    }

    private val mRequestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) scanBluetoothDevices()
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

        // 付近のBluetooth端末を取得
        mBluetoothScanController.devicesLiveData.observe(viewLifecycleOwner) { devices ->
            devices.forEach {
                mViewModel.onReceiveBluetoothResult(it.device, it.rssi)
            }
        }

        // Bluetooth端末の検索に必要なパーミッションの取得
        val accessFileLocation = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (accessFileLocation == PackageManager.PERMISSION_GRANTED) scanBluetoothDevices()
        else mRequestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        return mBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        mBluetoothScanController.cancelDiscovery()
    }

    private fun scanBluetoothDevices() {
        mBluetoothScanController.startDiscovery()
    }

    // ============================
    // UserViewHolder.CallbackListener
    // ============================
    override fun onClick(data: UserEntity) {
        findNavController().navigate(
            SelectNameFragmentDirections.selectToTemperature(data.name)
        )
    }
}