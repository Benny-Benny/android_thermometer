package jp.aoyama.mki.thermometer.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import jp.aoyama.mki.thermometer.models.BluetoothData
import jp.aoyama.mki.thermometer.models.User
import jp.aoyama.mki.thermometer.repository.LocalFileUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreateUserSharedViewModel : ViewModel() {
    private data class CreateUserState(
        var name: String? = null,
        var bluetoothDeviceName: String? = null,
        var bluetoothMacAddress: String? = null,
    )

    private var state = CreateUserState()

    var name: String?
        get() = state.name
        set(value) {
            state.name = value
        }

    var bluetoothDeviceName: String?
        get() = state.bluetoothDeviceName
        set(value) {
            state.bluetoothDeviceName = value
        }

    var bluetoothMacAddress: String?
        get() = state.bluetoothMacAddress
        set(value) {
            state.bluetoothMacAddress = value
        }

    suspend fun createUser(context: Context) = withContext(Dispatchers.IO) {
        val repository = LocalFileUserRepository(context)
        if (name != null) {
            val bluetooth = BluetoothData.create(bluetoothDeviceName, bluetoothMacAddress)
            val user = User(name = name!!, bluetoothData = bluetooth)
            repository.save(user)
            state = CreateUserState()
        }
    }
}