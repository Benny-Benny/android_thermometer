package jp.aoyama.mki.thermometer.repository

import android.content.Context
import com.google.gson.Gson
import jp.aoyama.mki.thermometer.models.User

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