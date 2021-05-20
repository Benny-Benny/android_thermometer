package jp.aoyama.mki.thermometer.fragment.user.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import jp.aoyama.mki.thermometer.databinding.SelectNameFragmentBinding
import jp.aoyama.mki.thermometer.models.UserEntity
import jp.aoyama.mki.thermometer.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class SelectNameFragment : Fragment(), UserViewHolder.CallbackListener {
    private val mViewModel: UserViewModel by viewModels()
    private lateinit var mBinding: SelectNameFragmentBinding
    private val mAdapterNearUser: UserListAdapter = UserListAdapter(this)
    private val mAdapterOtherUser: UserListAdapter = UserListAdapter(this)

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
            listOutUser.adapter = mAdapterOtherUser
        }

        lifecycleScope.launch {
            mViewModel.getUsers(requireContext()).observe(viewLifecycleOwner) { data ->
                mAdapterNearUser.submitList(data.near)
                mAdapterOtherUser.submitList(data.outs)
            }
        }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding.floatingActionButton.setOnClickListener {
            findNavController().navigate(SelectNameFragmentDirections.selectToEdit())
            super.onViewCreated(view, savedInstanceState)
        }
    }

    // ============================
    // UserViewHolder.CallbackListener
    // ============================

    override fun onClick(data: UserEntity) {
        findNavController().navigate(SelectNameFragmentDirections.selectToTemperature(data.name))
    }
}