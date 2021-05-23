package jp.aoyama.mki.thermometer.infrastructure.user

import android.content.Context
import com.google.gson.Gson
import jp.aoyama.mki.thermometer.domain.models.BluetoothData
import jp.aoyama.mki.thermometer.domain.models.Grade
import jp.aoyama.mki.thermometer.domain.models.UserEntity
import jp.aoyama.mki.thermometer.domain.repository.UserRepository

class LocalFileUserRepository(private val context: Context) : UserRepository {

    private val mGson: Gson = Gson()
    private val mFileInputStream get() = context.openFileInput(FILE_NAME)
    private val mFileOutputStream get() = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)

    override suspend fun findAll(): List<UserEntity> {
        return try {
            val nameJson = mFileInputStream.bufferedReader().readLine() ?: "[]"
            mGson.fromJson(nameJson, Array<UserEntity>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun find(userId: String): UserEntity? {
        return findAll().find { user -> user.id == userId }
    }

    override suspend fun save(user: UserEntity) {
        val users = findAll().toMutableList()
        users.add(user)

        val json = mGson.toJson(users)
        mFileOutputStream.use {
            it.write(json.toByteArray())
        }
    }

    override suspend fun updateName(userId: String, name: String) {
        val users = findAll().toMutableList()
        val user = users.find { it.id == userId } ?: return
        users.removeAll { it.id == userId }
        users.add(user.copy(name = name))

        val json = mGson.toJson(users)
        mFileOutputStream.use {
            it.write(json.toByteArray())
        }
    }

    override suspend fun updateGrade(userId: String, grade: Grade?) {
        val users = findAll().toMutableList()
        val user = users.find { it.id == userId } ?: return
        users.removeAll { it.id == userId }
        users.add(user.copy(grade = grade?.gradeName))

        val json = mGson.toJson(users)
        mFileOutputStream.use {
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

        val json = mGson.toJson(users)
        mFileOutputStream.use {
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

        val json = mGson.toJson(users)
        mFileOutputStream.use {
            it.write(json.toByteArray())
        }
    }

    override suspend fun delete(userId: String) {
        val users = findAll().toMutableList()
        users.removeAll { it.id == userId }

        val json = mGson.toJson(users)
        mFileOutputStream.use {
            it.write(json.toByteArray())
        }
    }

    companion object {
        private const val FILE_NAME = "Name List.txt"
    }
}