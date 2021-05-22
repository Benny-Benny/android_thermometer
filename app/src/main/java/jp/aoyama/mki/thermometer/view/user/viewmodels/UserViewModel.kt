package jp.aoyama.mki.thermometer.view.user.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.aoyama.mki.thermometer.infrastructure.user.LocalFileUserRepository
import jp.aoyama.mki.thermometer.view.bluetooth.scanner.BluetoothDeviceData
import jp.aoyama.mki.thermometer.view.models.UserData
import jp.aoyama.mki.thermometer.view.models.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class UserViewModel : ViewModel() {
    private val _mUserData: MutableLiveData<UserData> = MutableLiveData()

    fun onReceiveBluetoothResult(devices: List<BluetoothDeviceData>) {
        val data = _mUserData.value ?: return

        devices.map { bluetoothData ->
            val device = bluetoothData.device
            val user = data.users.find { it.bluetoothData?.address == device.address } ?: return@map
            val updated = user.copy(
                rssi = bluetoothData.rssi,
                lastFoundAt = Calendar.getInstance()
            )
            data.addNearUser(updated)
        }

        data.checkExpired()
        _mUserData.value = data
    }

    private suspend fun getUsers(context: Context): UserData {
        val userRepository = LocalFileUserRepository(context)
        val users = withContext(Dispatchers.IO) { userRepository.findAll() }
        val entities = users.map { UserEntity(it, null, null) }
        val data = UserData.create(near = mutableListOf(), outs = entities)
        _mUserData.value = data
        return data
    }

    fun observeUsers(context: Context): LiveData<UserData> {
        viewModelScope.launch { getUsers(context) }
        return _mUserData
    }

    suspend fun deleteUser(context: Context, userId: String) {
        val userRepository = LocalFileUserRepository(context)

        withContext(Dispatchers.IO) {
            userRepository.delete(userId)
        }
    }
}