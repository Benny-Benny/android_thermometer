package jp.aoyama.mki.thermometer.view.user.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.aoyama.mki.thermometer.domain.models.BluetoothData
import jp.aoyama.mki.thermometer.domain.models.BluetoothDeviceData
import jp.aoyama.mki.thermometer.domain.models.Grade
import jp.aoyama.mki.thermometer.domain.models.User
import jp.aoyama.mki.thermometer.domain.service.UserService
import jp.aoyama.mki.thermometer.infrastructure.csv.user.UserCSVUtil
import jp.aoyama.mki.thermometer.view.models.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class UserViewModel : ViewModel() {
    private val _mUserData: MutableLiveData<UserData> = MutableLiveData()
    private val service = UserService()

    fun onReceiveBluetoothResult(devices: List<BluetoothDeviceData>) {
        var data = _mUserData.value ?: return

        devices.map { bluetoothData ->
            val device = bluetoothData.device
            val user = data.users.find { user ->
                user.bluetoothDevices.any { it.address == device.address }
            } ?: return@map

            val updated = user.copy(lastFoundAt = Calendar.getInstance())
            data = data.addNearUser(updated)
        }

        _mUserData.value = data
    }

    fun observeUsers(): LiveData<UserData> {
        viewModelScope.launch { getUsers() }
        return _mUserData
    }

    private suspend fun getUsers(): UserData {
        val users = service.getUsers()
        _mUserData.value = users
        return users
    }

    suspend fun getUser(userId: String): User? {
        return service.getUser(userId)

    }

    suspend fun updateName(userId: String, name: String) {
        service.updateName(userId, name)
    }

    suspend fun updateGrade(userId: String, grade: Grade?) {
        service.updateGrade(userId, grade)
    }

    suspend fun addBluetoothDevice(userId: String, device: BluetoothData) {
        service.addBluetoothDevice(userId, device)
    }

    suspend fun removeBluetoothDevice(userId: String, address: String) {
        service.removeBluetoothDevice(userId, address)
    }

    /**
     * CSVファイルからユーザを追加
     */
    suspend fun importFromCSV(context: Context, uri: Uri) {
        val users = withContext(Dispatchers.IO) {
            UserCSVUtil().importFromCsv(context, uri)
        }

        users.map { user -> service.addUser(user) }
    }

    suspend fun deleteUser(userId: String) {
        service.deleteUser(userId)
    }
}