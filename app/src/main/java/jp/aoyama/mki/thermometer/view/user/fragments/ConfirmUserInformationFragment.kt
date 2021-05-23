package jp.aoyama.mki.thermometer.view.user.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.databinding.FragmentConfirmUserInformationBinding
import jp.aoyama.mki.thermometer.view.user.viewmodels.CreateUserSharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmUserInformationFragment : Fragment() {

    private val mViewModel: CreateUserSharedViewModel by viewModels({ requireActivity() })
    private lateinit var mBinding: FragmentConfirmUserInformationBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentConfirmUserInformationBinding.inflate(inflater, container, false)

        mBinding.apply {
            textName.text = mViewModel.name
            if (mViewModel.bluetoothMacAddress != null) {
                textBluetoothName.text = mViewModel.bluetoothDeviceName
                textBluetoothAddress.text = mViewModel.bluetoothMacAddress
            } else {
                textBluetoothName.text = "登録されていません。"
            }

            buttonSave.setOnClickListener {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        mViewModel.createUser(requireContext())
                    }
                    findNavController().popBackStack(R.id.homeFragment, false)
                }
            }
            buttonBack.setOnClickListener { findNavController().popBackStack() }

        }

        return mBinding.root
    }
}