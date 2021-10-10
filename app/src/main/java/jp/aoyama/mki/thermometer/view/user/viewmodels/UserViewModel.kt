package jp.aoyama.mki.thermometer.view.user.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import jp.aoyama.mki.thermometer.domain.models.*
import jp.aoyama.mki.thermometer.domain.models.device.BluetoothScanResult
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.domain.models.user.User
import jp.aoyama.mki.thermometer.domain.service.UserService
import jp.aoyama.mki.thermometer.view.models.UserEntity
import jp.aoyama.mki.thermometer.view.models.UserEntity.Companion.updateUser
import kotlinx.coroutines.launch
import java.util.*

class UserViewModel : ViewModel() {
    private val _mUserData: MutableLiveData<List<UserEntity>> = MutableLiveData()

    fun onReceiveBluetoothResult(devices: List<BluetoothScanResult>) {
        var users = _mUserData.value ?: return

        devices.forEach { device ->
            val user = users.find {
                val userDevice = it.device ?: return@find false
                userDevice.address == device.address
            } ?: return@forEach
            users = users.updateUser(user.copy(lastFoundAt = Calendar.getInstance()))
        }

        _mUserData.value = users
    }

    fun observeUsers(context: Context): LiveData<List<UserEntity>> {
        viewModelScope.launch {
            reloadUserData(context)
        }

        return _mUserData.map { users ->
            val sortByName = users.sortedBy { it.name.toLowerCase(Locale.getDefault()) }
            val foundUsers = sortByName.filter { it.found }
            val notFoundUsers = sortByName - foundUsers
            foundUsers + notFoundUsers
        }
    }

    suspend fun reloadUserData(context: Context) {
        _mUserData.value = getUsers(context)
    }

    private suspend fun getUsers(context: Context): List<UserEntity> {
        val service = UserService(context)

        return try {
            service.getUsers()
        } catch (e: Exception) {
            Toast.makeText(context, "データ取得中にエラーが発生しました。", Toast.LENGTH_LONG).show()
            Log.i(TAG, "getUsers: error while getting users", e)
            emptyList()
        }
    }

    suspend fun getUser(context: Context, userId: String): User? {
        val service = UserService(context)
        return kotlin.runCatching {
            service.getUser(userId)
        }.fold(
            onSuccess = { it },
            onFailure = { e ->
                Toast.makeText(context, "データ取得中にエラーが発生しました。", Toast.LENGTH_LONG).show()
                Log.i(TAG, "getUser: error while getting users", e)
                null
            }
        )
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

    suspend fun removeBluetoothDevice(context: Context, userId: String) {
        val service = UserService(context)
        service.removeBluetoothDevice(userId)
    }

    suspend fun deleteUser(context: Context, userId: String) {
        val service = UserService(context)
        service.deleteUser(userId)
    }

    companion object {
        private const val TAG = "UserViewModel"
    }
}