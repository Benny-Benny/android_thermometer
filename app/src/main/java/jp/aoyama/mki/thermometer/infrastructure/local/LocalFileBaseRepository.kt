package jp.aoyama.mki.thermometer.infrastructure.local

import android.content.Context
import android.util.Log
import com.google.gson.Gson

class LocalFileBaseRepository<T>(
    private val mContext: Context,
    private val fileName: String,
    private val fromJson: (gson: Gson, json: String) -> List<T>
) {
    private val mGson = Gson()
    private val mFileInputStream get() = mContext.openFileInput(fileName)
    private val mFileOutputStream get() = mContext.openFileOutput(fileName, Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "LocalFileBaseRepository"
    }

    fun findAll(): List<T> {
        return kotlin.runCatching {
            val json = mFileInputStream.bufferedReader().readLine() ?: "[]"
            fromJson(mGson, json)
        }.fold(
            onSuccess = { it },
            onFailure = { e ->
                Log.e(TAG, "findAll: error while getting data", e)
                emptyList()
            }
        )
    }

    fun findAll(check: (T) -> Boolean): List<T> {
        return findAll().filter { check(it) }
    }

    fun find(check: (T) -> Boolean): T? {
        return findAll().find { check(it) }
    }

    fun save(data: T) {
        val savedData = findAll().toMutableList()
        savedData.add(data)

        val json = mGson.toJson(savedData)
        mFileOutputStream.use {
            it.write(json.toByteArray())
        }
    }

    fun delete(check: (T) -> Boolean) {
        val data = findAll().toMutableList()
        data.removeAll { check(it) }

        val json = mGson.toJson(data)
        mFileOutputStream.use {
            it.write(json.toByteArray())
        }
    }

    fun update(check: (T) -> Boolean, onUpdate: (T) -> T) {
        val date = find(check) ?: return
        delete(check)
        save(onUpdate(date))
    }
}