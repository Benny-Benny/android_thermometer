package jp.aoyama.mki.thermometer.view.user.fragments.create

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
import kotlinx.coroutines.launch

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
            textGrade.text = mViewModel.grade?.gradeName ?: "選択されていません"

            textBluetoothName.text = mViewModel.bluetoothDeviceName ?: "登録されていません。"
            textBluetoothAddress.text = mViewModel.bluetoothMacAddress

            buttonSave.setOnClickListener { createUser() }
            buttonBack.setOnClickListener { findNavController().popBackStack() }

        }

        return mBinding.root
    }

    private fun createUser() {
        lifecycleScope.launch {
            mBinding.progressCircular.visibility = View.VISIBLE
            mViewModel.createUser(requireContext())
            mBinding.progressCircular.visibility = View.GONE
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }
}