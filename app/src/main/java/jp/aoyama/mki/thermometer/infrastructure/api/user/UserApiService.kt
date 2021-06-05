package jp.aoyama.mki.thermometer.infrastructure.api.user

import jp.aoyama.mki.thermometer.infrastructure.api.user.models.*
import retrofit2.Call
import retrofit2.http.*

interface UserApiService {
    @GET("/users")
    fun findAll(): Call<List<User>>

    @GET("/users/{id}")
    fun find(@Path("id") userId: String): Call<User>

    @POST("/users/create")
    fun create(@Body request: CreateUserRequest): Call<Unit>

    @POST("/users/edit/{id}")
    fun editName(@Path("id") userId: String, @Body request: EditNameRequest): Call<Unit>

    @POST("/users/edit/{id}")
    fun editGrade(@Path("id") userId: String, @Body request: EditGradeRequest): Call<Unit>

    @POST("/users/edit/{id}")
    fun editDevices(@Path("id") userId: String, @Body request: EditDeviceRequest): Call<Unit>

    @DELETE("/users/delete/{id}")
    fun deleteUser(@Path("id") userId: String): Call<Unit>
}

