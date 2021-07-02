package jp.aoyama.mki.thermometer.view.user.fragments.create

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.databinding.FragmentEditNameBinding
import jp.aoyama.mki.thermometer.view.user.viewmodels.CreateUserSharedViewModel

class EditNameFragment : Fragment() {

    private val mViewModel: CreateUserSharedViewModel by viewModels({ requireActivity() })
    private lateinit var mBinding: FragmentEditNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEditNameBinding.inflate(inflater, container, false)

        mBinding.apply {
            buttonNext.setOnClickListener { saveName() }

            editTextName.setText(mViewModel.name)

            // Enterを押したときに、キーボードを閉じる
            editTextName.setOnEditorActionListener { v, _, event ->
                val inputManager =
                    getSystemService(requireContext(), InputMethodManager::class.java)
                        ?: return@setOnEditorActionListener false

                if (event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                    inputManager.hideSoftInputFromWindow(v.windowToken, 0)
                    return@setOnEditorActionListener true
                }

                return@setOnEditorActionListener false
            }
        }
        return mBinding.root
    }

    private fun saveName() {
        val name = mBinding.editTextName.text.toString()
        if (name.isEmpty()) {
            mBinding.editTextName.error = getString(R.string.name_error)
            return
        }
        mViewModel.name = name
        findNavController().navigate(EditNameFragmentDirections.editNameToEditGrade())
    }
}
