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
import jp.aoyama.mki.thermometer.databinding.TemperatureFragmentBinding
import jp.aoyama.mki.thermometer.util.CSVFileManager
import jp.aoyama.mki.thermometer.viewmodels.TmpViewModel


class TemperatureFragment : Fragment() {

    private lateinit var mName: String
    private val mCsvFileManager = CSVFileManager()

    private lateinit var binding: TemperatureFragmentBinding
    private val args by navArgs<TemperatureFragmentArgs>()

    private val viewModel: TmpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = TemperatureFragmentBinding.inflate(layoutInflater, container, false)
        mName = args.name

        binding.apply {
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
            button38.setOnClickListener {binding.textView.text = ("")}
            button37.setOnClickListener {binding.textView.append(".")}
            button40.setOnClickListener {onSaveClick()}
        }
        return binding.root
    }


    fun onNumberClick(number: Int){
        binding.textView.append(number.toString())
    }

    fun onSaveClick(){
        val textView = binding.textView

        val value = textView.text.toString().toFloatOrNull()

        // 入力値の検証
        if(value == null){
            Toast.makeText(requireContext(), "体温計の通りに入力してください", Toast.LENGTH_LONG).show()
            return
        }else if(value > 45f || value < 35f) {
            Toast.makeText(requireContext(), "体温計の通りに入力してください", Toast.LENGTH_LONG).show()
            textView.text = ""
            return
        }

        viewModel.saveTemperature(requireContext(), mName, value)

        // 編集画面を閉じる
        Toast.makeText(requireContext(),"体温を保存しました", Toast.LENGTH_LONG).show()
        findNavController().popBackStack()
    }

}