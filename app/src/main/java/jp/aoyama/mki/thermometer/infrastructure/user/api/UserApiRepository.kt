package jp.aoyama.mki.thermometer.infrastructure.user.api

import android.util.Log
import jp.aoyama.mki.thermometer.domain.models.BluetoothData
import jp.aoyama.mki.thermometer.domain.models.Grade
import jp.aoyama.mki.thermometer.domain.models.UserEntity
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.user.api.models.CreateUserRequest
import jp.aoyama.mki.thermometer.infrastructure.user.api.models.EditDeviceRequest
import jp.aoyama.mki.thermometer.infrastructure.user.api.models.EditGradeRequest
import jp.aoyama.mki.thermometer.infrastructure.user.api.models.EditNameRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserApiRepository : UserRepository {

    private val service: UserApiService by lazy {
        val client = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(client)
            .build()

        retrofit.create(UserApiService::class.java)
    }

    override suspend fun findAll(): List<UserEntity> {
        val response = try {
            service.findAll().execute().body()
        } catch (e: Exception) {
            Log.e(TAG, "findAll: error while fetching users.", e)
            null
        } ?: return emptyList()

        return response.map { it.toUserEntity() }
    }

    override suspend fun find(userId: String): UserEntity? {
        val response = try {
            service.find(userId).execute().body()
        } catch (e: Exception) {
            Log.e(TAG, "findAll: error while fetching user.", e)
            null
        } ?: return null

        return response.toUserEntity()
    }

    override suspend fun save(user: UserEntity) {
        val request = CreateUserRequest(
            name = user.name,
            devices = user.bluetoothDevices.map { it.address },
            grade = user.grade
        )

        service.create(request).execute()
    }

    override suspend fun updateName(userId: String, name: String) {
        val request = EditNameRequest(name = name)
        service.editName(userId, request).execute()
    }

    override suspend fun updateGrade(userId: String, grade: Grade?) {
        val request = EditGradeRequest(grade = grade?.gradeName)
        service.editGrade(userId, request).execute()
    }

    override suspend fun addBluetoothDevice(userId: String, bluetooth: BluetoothData) {
        val user = find(userId) ?: return
        val devices = user.bluetoothDevices.toMutableList()
        devices.add(bluetooth)
        val request = EditDeviceRequest(devices = devices.map { it.address })
        service.editDevices(userId, request)
    }

    override suspend fun deleteBluetoothDevice(userId: String, address: String) {
        val user = find(userId) ?: return
        val devices = user.bluetoothDevices.toMutableList()
        devices.removeAll { it.address == address }
        val request = EditDeviceRequest(devices = devices.map { it.address })
        service.editDevices(userId, request).execute()
    }

    override suspend fun delete(userId: String) {
        service.deleteUser(userId).execute()
    }

    companion object {
        private const val TAG = "UserApiRepository"
        private const val BASE_URL = "http://192.168.0.13:5000/users"
    }
}