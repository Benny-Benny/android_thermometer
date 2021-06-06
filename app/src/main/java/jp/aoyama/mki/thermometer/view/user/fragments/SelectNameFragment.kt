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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import jp.aoyama.mki.thermometer.databinding.FragmentSelectNameBinding
import jp.aoyama.mki.thermometer.domain.repository.BluetoothDeviceScanner
import jp.aoyama.mki.thermometer.infrastructure.api.bluetooth.ApiBluetoothScanner
import jp.aoyama.mki.thermometer.view.home.HomeFragmentDirections
import jp.aoyama.mki.thermometer.view.models.UserEntity
import jp.aoyama.mki.thermometer.view.user.list.UserListAdapter
import jp.aoyama.mki.thermometer.view.user.list.UserViewHolder
import jp.aoyama.mki.thermometer.view.user.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class SelectNameFragment : Fragment(), UserViewHolder.CallbackListener {
    private val mViewModel: UserViewModel by viewModels()
    private lateinit var mBinding: FragmentSelectNameBinding
    private val mAdapterNearUser: UserListAdapter = UserListAdapter(this)
    private val mAdapterOutUser: UserListAdapter = UserListAdapter(this)

    private val mBluetoothDeviceScanner: BluetoothDeviceScanner by lazy {
        ApiBluetoothScanner()
    }

    private val mRequestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) scanBluetoothDevices()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSelectNameBinding.inflate(inflater, container, false)
        mBinding.apply {
            listNearUser.layoutManager = LinearLayoutManager(requireContext())
            listNearUser.adapter = mAdapterNearUser
            (listNearUser.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            listOutUser.layoutManager = LinearLayoutManager(requireContext())
            listOutUser.adapter = mAdapterOutUser

            floatingActionButton.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.homeToCreateUser()
                )
            }
        }

        mViewModel.observeUsers(requireContext()).observe(viewLifecycleOwner) { data ->
            mAdapterNearUser.submitList(data.near)
            mAdapterOutUser.submitList(data.outs)
        }

        lifecycleScope.launch {
            mBluetoothDeviceScanner.devicesLiveData.observe(viewLifecycleOwner) { devices ->
                mViewModel.onReceiveBluetoothResult(devices)
            }
        }

        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        // Bluetooth端末の検索に必要なパーミッションの取得
        val accessFileLocation = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (accessFileLocation == PackageManager.PERMISSION_GRANTED) scanBluetoothDevices()
        else mRequestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onPause() {
        super.onPause()
        mBluetoothDeviceScanner.cancelDiscovery()
    }

    private fun scanBluetoothDevices() {
        mBluetoothDeviceScanner.startDiscovery()
    }

    // ============================
    // UserViewHolder.CallbackListener
    // ============================
    override fun onClick(data: UserEntity) {
        findNavController().navigate(
            HomeFragmentDirections.homeToMeasure(data.id)
        )
    }

    override fun onEdit(data: UserEntity) {
        findNavController().navigate(
            HomeFragmentDirections.homeToEditUser(data.id)
        )
    }

    companion object {
        fun newInstance(): SelectNameFragment {
            return SelectNameFragment()
        }
    }
}