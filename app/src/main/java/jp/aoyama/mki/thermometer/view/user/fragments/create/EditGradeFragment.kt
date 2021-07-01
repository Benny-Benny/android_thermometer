package jp.aoyama.mki.thermometer.view.user.fragments.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import jp.aoyama.mki.thermometer.databinding.FragmentEditGradeBinding
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.view.user.viewmodels.CreateUserSharedViewModel

class EditGradeFragment : Fragment() {

    private val mViewModel: CreateUserSharedViewModel by viewModels({ requireActivity() })
    private lateinit var mBinding: FragmentEditGradeBinding

    private val spinnerItemClickListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            updateGrade(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEditGradeBinding.inflate(inflater, container, false)

        mBinding.apply {
            buttonNext.setOnClickListener { saveGrade() }

            spinnerGrade.onItemSelectedListener = spinnerItemClickListener
            spinnerGrade.adapter = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
            ).apply {
                val grades = Grade.values().map { it.gradeName }.toMutableList()
                add("選択されていません")
                addAll(grades)

                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }
        return mBinding.root
    }

    private fun updateGrade(position: Int) {
        val grade = when (position) {
            in 1..Grade.values().size -> Grade.values()[position - 1]
            else -> null
        }
        mViewModel.grade = grade
    }

    private fun saveGrade() {
        findNavController().navigate(EditGradeFragmentDirections.editGradeToPairing())
    }
}
