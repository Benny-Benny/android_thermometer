package jp.aoyama.mki.thermometer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.databinding.TemperatureFragmentBinding
import jp.aoyama.mki.thermometer.viewmodels.TemperatureViewModel


class TemperatureFragment : Fragment() {

    private lateinit var mBinding: TemperatureFragmentBinding

    private val mViewModel: TemperatureViewModel by viewModels()
    private val mArgs by navArgs<TemperatureFragmentArgs>()
    private val mName: String get() = mArgs.name // 体温を計測する人の名前


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = TemperatureFragmentBinding.inflate(layoutInflater, container, false)

        mBinding.apply {
            button.setOnClickListener { onNumberClick(1) }
            button2.setOnClickListener { onNumberClick(2) }
            button3.setOnClickListener { onNumberClick(3) }
            button30.setOnClickListener { onNumberClick(4) }
            button31.setOnClickListener { onNumberClick(5) }
            button32.setOnClickListener { onNumberClick(6) }
            button34.setOnClickListener { onNumberClick(7) }
            button33.setOnClickListener { onNumberClick(8) }
            button35.setOnClickListener { onNumberClick(9) }
            button36.setOnClickListener { onNumberClick(0) }
            button38.setOnClickListener { mBinding.textView.text = ("") }
            button37.setOnClickListener { mBinding.textView.append(".") }
            button40.setOnClickListener { onSaveClick() }
        }
        return mBinding.root
    }

    private fun onNumberClick(number: Int) {
        mBinding.textView.append(number.toString())
    }

    private fun onSaveClick() {
        val inputValueStr = mBinding.textView.text.toString()

        // 入力値の検証と保存
        val valid = mViewModel.saveTemperature(requireContext(), mName, inputValueStr)
        if (!valid) {
            Toast.makeText(
                requireContext(),
                R.string.body_temperature_input_alert,
                Toast.LENGTH_LONG
            ).show()
            mBinding.textView.text = ""
            return
        }

        // 編集画面を閉じる
        Toast.makeText(requireContext(), R.string.saved_body_temperature, Toast.LENGTH_LONG).show()
        findNavController().popBackStack()
    }

}