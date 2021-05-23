package jp.aoyama.mki.thermometer.view.user.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.aoyama.mki.thermometer.domain.models.BluetoothData
import jp.aoyama.mki.thermometer.domain.models.Grade
import jp.aoyama.mki.thermometer.domain.models.User
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.user.LocalFileUserRepository
import jp.aoyama.mki.thermometer.infrastructure.user.UserCSVUtil
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
        var data = _mUserData.value ?: return

        devices.map { bluetoothData ->
            val device = bluetoothData.device
            val user = data.users.find { user ->
                user.bluetoothDevices.any { it.address == device.address }
            } ?: return@map

            val updated = user.copy(
                rssi = bluetoothData.rssi,
                lastFoundAt = Calendar.getInstance()
            )
            data = data.addNearUser(updated)
        }

        _mUserData.value = data
    }

    fun observeUsers(context: Context): LiveData<UserData> {
        viewModelScope.launch { getUsers(context) }
        return _mUserData
    }

    suspend fun getUsers(context: Context): UserData {
        val userRepository = LocalFileUserRepository(context)
        val users = withContext(Dispatchers.IO) { userRepository.findAll() }
        val entities = users.map { UserEntity(it.user, null, null) }
        val data = UserData(users = entities)
        _mUserData.value = data
        return data
    }

    suspend fun getUser(context: Context, userId: String): User? {
        val userRepository: UserRepository = LocalFileUserRepository(context)
        return withContext(Dispatchers.IO) {
            userRepository.find(userId)?.user
        }
    }

    suspend fun updateName(context: Context, userId: String, name: String) {
        val userRepository: UserRepository = LocalFileUserRepository(context)
        withContext(Dispatchers.IO) {
            userRepository.updateName(userId, name)
        }
    }

    suspend fun updateGrade(context: Context, userId: String, grade: Grade?) {
        val userRepository: UserRepository = LocalFileUserRepository(context)
        withContext(Dispatchers.IO) {
            userRepository.updateGrade(userId, grade)
        }
    }

    suspend fun addBluetoothDevice(context: Context, userId: String, device: BluetoothData) {
        val userRepository: UserRepository = LocalFileUserRepository(context)
        withContext(Dispatchers.IO) {
            userRepository.addBluetoothDevice(userId, device)
        }
    }

    suspend fun removeBluetoothDevice(context: Context, userId: String, address: String) {
        val userRepository: UserRepository = LocalFileUserRepository(context)
        withContext(Dispatchers.IO) {
            userRepository.deleteBluetoothDevice(userId, address)
        }
    }

    /**
     * CSVファイルからユーザを追加
     */
    suspend fun importFromCSV(context: Context, uri: Uri) {
        val users = withContext(Dispatchers.IO) {
            UserCSVUtil().importFromCsv(context, uri)
        }

        val userRepository: UserRepository = LocalFileUserRepository(context)
        withContext(Dispatchers.IO) {
            users.map { user ->
                userRepository.save(user)
            }
        }
    }

    suspend fun deleteUser(context: Context, userId: String) {
        val userRepository = LocalFileUserRepository(context)

        withContext(Dispatchers.IO) {
            userRepository.delete(userId)
        }
    }
}