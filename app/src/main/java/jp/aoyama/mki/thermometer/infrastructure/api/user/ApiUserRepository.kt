package jp.aoyama.mki.thermometer.infrastructure.api.user

import android.util.Log
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.domain.models.user.UserEntity
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.api.ApiRepositoryUtil
import jp.aoyama.mki.thermometer.infrastructure.api.user.models.CreateUserRequest
import jp.aoyama.mki.thermometer.infrastructure.api.user.models.EditGradeRequest
import jp.aoyama.mki.thermometer.infrastructure.api.user.models.EditNameRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiUserRepository : UserRepository {

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
            id = user.id,
            name = user.name,
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

    override suspend fun delete(userId: String) {
        service.deleteUser(userId).execute()
    }

    companion object {
        private const val TAG = "UserApiRepository"
        private const val BASE_URL = ApiRepositoryUtil.BASE_URL
    }
}