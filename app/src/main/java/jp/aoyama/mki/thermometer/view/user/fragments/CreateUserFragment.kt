package jp.aoyama.mki.thermometer.view.user.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import jp.aoyama.mki.thermometer.databinding.FragmentCreateUserBinding
import jp.aoyama.mki.thermometer.view.user.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class CreateUserFragment : Fragment() {
    private lateinit var mBinding: FragmentCreateUserBinding
    private val mViewModel: UserViewModel by viewModels()

    private val mChooseCSVFile =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@registerForActivityResult
            importFromCsv(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCreateUserBinding.inflate(inflater, container, false)

        mBinding.apply {
            buttonCreateNew.setOnClickListener {
                findNavController().navigate(CreateUserFragmentDirections.createToEdit())
            }

            buttonImport.setOnClickListener { openFileChooser() }
        }
        return mBinding.root
    }

    private fun openFileChooser() {
        mChooseCSVFile.launch("text/csv")
    }

    private fun importFromCsv(uri: Uri) {
        lifecycleScope.launch {
            mBinding.progressBar.visibility = View.VISIBLE

            mViewModel.importFromCSV(requireContext(), uri)

            mBinding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "ユーザー情報を保存しました", Toast.LENGTH_LONG).show()

            findNavController().popBackStack()
        }
    }

}