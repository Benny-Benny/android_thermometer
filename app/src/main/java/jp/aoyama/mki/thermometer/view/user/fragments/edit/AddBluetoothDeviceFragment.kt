package jp.aoyama.mki.thermometer.view.user.fragments.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import jp.aoyama.mki.thermometer.databinding.FeagmentAddBluetoothDeviceBinding
import jp.aoyama.mki.thermometer.domain.models.device.BluetoothScanResult
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.repository.BluetoothDeviceScanner
import jp.aoyama.mki.thermometer.infrastructure.android.bluetooth.BluetoothDiscoveryDeviceScanner
import jp.aoyama.mki.thermometer.view.bluetooth.list.BluetoothListAdapter
import jp.aoyama.mki.thermometer.view.bluetooth.list.BluetoothViewHolder
import jp.aoyama.mki.thermometer.view.user.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class AddBluetoothDeviceFragment : Fragment(), BluetoothViewHolder.CallbackListener {

    private val mViewModel: UserViewModel by viewModels()
    private lateinit var mBinding: FeagmentAddBluetoothDeviceBinding
    private lateinit var mAdapter: BluetoothListAdapter
    private lateinit var mBluetoothDeviceScanner: BluetoothDeviceScanner

    private val args: AddBluetoothDeviceFragmentArgs by navArgs()
    private val userId get() = args.userId

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

        mBluetoothDeviceScanner = BluetoothDiscoveryDeviceScanner(requireContext())
        mBluetoothDeviceScanner.startDiscovery()
        mBluetoothDeviceScanner.devicesLiveData.observe(viewLifecycleOwner) { devices ->
            val namedDevices = devices.filter { it.name != null }

            mBinding.progressCircular.visibility =
                if (namedDevices.isEmpty()) View.VISIBLE
                else View.GONE

            mAdapter.submitList(namedDevices)
        }
        return mBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        mBluetoothDeviceScanner.cancelDiscovery()
    }

    override fun onClick(device: BluetoothScanResult) {
        lifecycleScope.launch {
            mBinding.progressCircular.visibility = View.VISIBLE
            mViewModel.addBluetoothDevice(
                requireContext(),
                Device(
                    address = device.address,
                    userId = userId
                )
            )
            findNavController().popBackStack()
            mBinding.progressCircular.visibility = View.GONE
        }
    }
}