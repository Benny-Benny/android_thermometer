package jp.aoyama.mki.thermometer.viewmodels

import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.aoyama.mki.thermometer.models.UserData
import jp.aoyama.mki.thermometer.models.UserEntity
import jp.aoyama.mki.thermometer.repository.LocalFileUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class UserViewModel : ViewModel() {
    private val _mUserData: MutableLiveData<UserData> = MutableLiveData()

    fun onReceiveBluetoothResult(result: ScanResult) {
        val data = _mUserData.value ?: return

        val address = result.device?.address
        val user = data.users.find { it.bluetoothData?.address == address }
        if (user == null) {
            val validate = data.checkExpired()
            if (!validate) _mUserData.value = data
            return
        }
        data.addNearUser(user.copy(rssi = result.rssi, lastFoundAt = Calendar.getInstance()))
        _mUserData.value = data
    }

    fun getUsers(context: Context): LiveData<UserData> {
        viewModelScope.launch {
            val userRepository = LocalFileUserRepository(context)
            val users = withContext(Dispatchers.IO) { userRepository.findAll() }
            val entities = users.map { UserEntity(it, null, null) }
            val data = UserData.create(near = mutableListOf(), outs = entities)
            _mUserData.value = data
        }
        return _mUserData
    }

    suspend fun deleteUser(context: Context, userId: String) {
        val userRepository = LocalFileUserRepository(context)

        withContext(Dispatchers.IO) {
            userRepository.delete(userId)
        }
    }

    companion object {
        private const val TAG = "UserViewModel"
    }
}