package jp.aoyama.mki.thermometer.view.user.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.domain.models.user.User
import jp.aoyama.mki.thermometer.domain.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class CreateUserSharedViewModel : ViewModel() {
    private data class CreateUserState(
        var name: String? = null,
        var grade: Grade? = null,
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

    var grade: Grade?
        get() = state.grade
        set(value) {
            state.grade = value
        }

    suspend fun createUser(context: Context) = withContext(Dispatchers.IO) {
        if (name != null) {
            val userId = UUID.randomUUID().toString()
            val bluetooth = Device.create(bluetoothDeviceName, userId, bluetoothMacAddress)
            val user = User(
                id = userId,
                name = name!!,
                grade = grade,
                devices = listOfNotNull(bluetooth)
            )

            val service = UserService(context)
            service.createUser(user)
            state = CreateUserState()
        }
    }
}