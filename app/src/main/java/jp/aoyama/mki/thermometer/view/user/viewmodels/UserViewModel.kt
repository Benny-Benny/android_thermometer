package jp.aoyama.mki.thermometer.view.user.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.aoyama.mki.thermometer.domain.models.*
import jp.aoyama.mki.thermometer.domain.models.device.BluetoothScanResult
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.domain.models.user.User
import jp.aoyama.mki.thermometer.domain.service.UserService
import jp.aoyama.mki.thermometer.infrastructure.local.user.UserCSVUtil
import jp.aoyama.mki.thermometer.view.models.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class UserViewModel : ViewModel() {
    private val _mUserData: MutableLiveData<UserData> = MutableLiveData()

    fun onReceiveBluetoothResult(devices: List<BluetoothScanResult>) {
        var data = _mUserData.value ?: return

        devices.map { device ->
            val user = data.users.find { user ->
                user.devices.any { it.address == device.address }
            } ?: return@map

            val updated = user.copy(lastFoundAt = Calendar.getInstance())
            data = data.addNearUser(updated)
        }

        _mUserData.value = data
    }

    fun observeUsers(context: Context): LiveData<UserData> {
        viewModelScope.launch { getUsers(context) }
        return _mUserData
    }

    private suspend fun getUsers(context: Context): UserData {
        val service = UserService(context)
        val users = service.getUsers()
        _mUserData.value = users
        return users
    }

    suspend fun getUser(context: Context, userId: String): User? {
        val service = UserService(context)
        return service.getUser(userId)

    }

    suspend fun updateName(context: Context, userId: String, name: String) {
        val service = UserService(context)
        service.updateName(userId, name)
    }

    suspend fun updateGrade(context: Context, userId: String, grade: Grade?) {
        val service = UserService(context)
        service.updateGrade(userId, grade)
    }

    suspend fun addBluetoothDevice(context: Context, device: Device) {
        val service = UserService(context)
        service.addBluetoothDevice(device)
    }

    suspend fun removeBluetoothDevice(context: Context, address: String) {
        val service = UserService(context)
        service.removeBluetoothDevice(address)
    }

    /**
     * CSVファイルからユーザを追加
     */
    suspend fun importFromCSV(context: Context, uri: Uri) {
        val users = withContext(Dispatchers.IO) {
            UserCSVUtil().importFromCsv(context, uri)
        }
        val service = UserService(context)
        users.map { user -> service.addUser(user) }
    }

    suspend fun deleteUser(context: Context, userId: String) {
        val service = UserService(context)
        service.deleteUser(userId)
    }
}