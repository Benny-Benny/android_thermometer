package jp.aoyama.mki.thermometer.view.user.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import jp.aoyama.mki.thermometer.domain.models.*
import jp.aoyama.mki.thermometer.domain.models.device.BluetoothScanResult
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.domain.models.user.User
import jp.aoyama.mki.thermometer.domain.service.UserService
import jp.aoyama.mki.thermometer.infrastructure.local.user.UserCSVUtil
import jp.aoyama.mki.thermometer.view.models.UserEntity
import jp.aoyama.mki.thermometer.view.models.UserEntity.Companion.updateUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class UserViewModel : ViewModel() {
    private val _mUserData: MutableLiveData<List<UserEntity>> = MutableLiveData()

    fun onReceiveBluetoothResult(devices: List<BluetoothScanResult>) {
        var users = _mUserData.value ?: return
        Log.d("VIEWMODEL", "onReceiveBluetoothResult: $devices")
        devices.map { device ->
            val user = users.find { user ->
                user.devices.any { it.address == device.address }
            } ?: return@map

            users = users.updateUser(user.copy(lastFoundAt = Calendar.getInstance()))
        }

        _mUserData.value = users
    }

    fun observeUsers(context: Context): LiveData<List<UserEntity>> {
        viewModelScope.launch { getUsers(context) }
        return _mUserData.map { users -> users.sortedBy { it.name.toLowerCase(Locale.getDefault()) } }
    }

    private suspend fun getUsers(context: Context): List<UserEntity> {
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
        users.map { user -> service.createUser(user) }
    }

    suspend fun deleteUser(context: Context, userId: String) {
        val service = UserService(context)
        service.deleteUser(userId)
    }
}