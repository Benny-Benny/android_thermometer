package jp.aoyama.mki.thermometer.infrastructure.user

import android.content.Context
import com.google.gson.Gson
import jp.aoyama.mki.thermometer.domain.models.BluetoothData
import jp.aoyama.mki.thermometer.domain.models.User
import jp.aoyama.mki.thermometer.domain.repository.UserRepository

class LocalFileUserRepository(private val context: Context) : UserRepository {

    private val mGson: Gson = Gson()

    override suspend fun findAll(): List<User> {
        return try {
            val nameJson = context.openFileInput(FILE_NAMES).bufferedReader().readLine() ?: "[]"
            mGson.fromJson(nameJson, Array<User>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun find(userId: String): User? {
        return findAll().find { user -> user.id == userId }
    }

    override suspend fun save(user: User) {
        val users = findAll().toMutableList()
        users.add(user)

        val json = Gson().toJson(users)
        context.openFileOutput(FILE_NAMES, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    override suspend fun updateName(userId: String, name: String) {
        val users = findAll().toMutableList()
        val user = users.find { it.id == userId } ?: return
        users.removeAll { it.id == userId }
        users.add(user.copy(name = name))

        val json = Gson().toJson(users)
        context.openFileOutput(FILE_NAMES, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    override suspend fun addBluetoothDevice(userId: String, bluetooth: BluetoothData) {
        val users = findAll().toMutableList()
        val user = users.find { it.id == userId } ?: return
        users.removeAll { it.id == userId }

        val devices = user.bluetoothDevices.toMutableList()
        devices.add(bluetooth)
        users.add(user.copy(bluetoothDevices = devices))

        val json = Gson().toJson(users)
        context.openFileOutput(FILE_NAMES, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    override suspend fun deleteBluetoothDevice(userId: String, address: String) {
        val users = findAll().toMutableList()
        val user = users.find { it.id == userId } ?: return
        users.removeAll { it.id == userId }

        val devices = user.bluetoothDevices.toMutableList()
        devices.removeAll { it.address == address }
        users.add(user.copy(bluetoothDevices = devices))

        val json = Gson().toJson(users)
        context.openFileOutput(FILE_NAMES, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    override suspend fun delete(userId: String) {
        val users = findAll().toMutableList()
        users.removeAll { it.id == userId }

        val json = Gson().toJson(users)
        context.openFileOutput(FILE_NAMES, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    companion object {
        private const val FILE_NAMES = "Name List.txt"
    }
}