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
import jp.aoyama.mki.thermometer.infrastructure.spreadsheet.bluetooth.SpreadSheetBluetoothScanner
import jp.aoyama.mki.thermometer.view.home.HomeFragmentDirections
import jp.aoyama.mki.thermometer.view.models.UserEntity
import jp.aoyama.mki.thermometer.view.user.list.UserListAdapter
import jp.aoyama.mki.thermometer.view.user.list.UserViewHolder
import jp.aoyama.mki.thermometer.view.user.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class SelectNameFragment : Fragment(), UserViewHolder.CallbackListener {
    private val mViewModel: UserViewModel by viewModels({ requireActivity() })
    private lateinit var mBinding: FragmentSelectNameBinding
    private val mUserListAdapter: UserListAdapter = UserListAdapter(this)

    private val mBluetoothDeviceScanner: BluetoothDeviceScanner by lazy {
        SpreadSheetBluetoothScanner(requireContext())
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
            recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewUsers.adapter = mUserListAdapter
            (recyclerViewUsers.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            floatingActionButton.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.homeToEditName())
            }
            progressCircular.visibility = View.VISIBLE
        }

        mViewModel.observeUsers(requireContext()).observe(viewLifecycleOwner) { users ->
            mBinding.progressCircular.visibility = View.GONE
            mUserListAdapter.submitList(users)

            // 更新があった時には、画面トップにスクロール
            mBinding.recyclerViewUsers.smoothScrollToPosition(0)
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

        mBinding.recyclerViewUsers.scrollToPosition(0)

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