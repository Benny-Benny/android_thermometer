package jp.aoyama.mki.thermometer.fragment.user

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import jp.aoyama.mki.thermometer.databinding.BluetoothPairingFragmentBinding

class BluetoothPairingFragment : Fragment() {

    private lateinit var mBinding: BluetoothPairingFragmentBinding

    private val mStartForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            findNavController().navigate(BluetoothPairingFragmentDirections.pairingToSelect())
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = BluetoothPairingFragmentBinding.inflate(inflater, container, false)

        mBinding.apply {
            buttonParing.setOnClickListener { launchParingScreen() }
            buttonSelectPaired.setOnClickListener {
                findNavController().navigate(BluetoothPairingFragmentDirections.pairingToSelect())
            }
            buttonSkip.setOnClickListener {
                findNavController().navigate(BluetoothPairingFragmentDirections.pairingToConfirm())
            }
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        return mBinding.root
    }

    private fun launchParingScreen() {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        mStartForResult.launch(intent)
    }
}