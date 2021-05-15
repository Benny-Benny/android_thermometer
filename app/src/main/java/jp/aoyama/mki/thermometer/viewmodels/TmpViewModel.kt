package jp.aoyama.mki.thermometer.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import jp.aoyama.mki.thermometer.models.TemperatureData
import jp.aoyama.mki.thermometer.util.CSVFileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TmpViewModel : ViewModel() {

    private val mCsvFileManager = CSVFileManager()

    fun addUser(context: Context, name: String) {
        val currentUsers = getUsers(context).toMutableList()
        currentUsers.add(name)

        val gson = Gson()
        var namesJson: String = gson.toJson(currentUsers)

        context.openFileOutput(FILE_NAMES,Context.MODE_PRIVATE).use{
            it.write( namesJson.toByteArray())
        }
    }

    fun deleteUser(context: Context, name: String) {
        val currentUsers = getUsers(context).toMutableList()
        currentUsers.removeAll { nameCurrent -> nameCurrent == name }

        val gson = Gson()
        var namesJson: String = gson.toJson(currentUsers)

        context.openFileOutput(FILE_NAMES,Context.MODE_PRIVATE).use{
            it.write( namesJson.toByteArray())
        }
    }

    fun saveTemperature(context: Context, name: String, temperature: Float){
        // 内部のＣＳＶファイルにデータを追加
        viewModelScope.launch(Dispatchers.IO) {
            mCsvFileManager.append(context, TemperatureData(name = name, temperature = temperature))
        }
    }

    fun getUsers(context: Context): List<String> {
        val gson = Gson()
        try {
            val nameJson = context.openFileInput(FILE_NAMES).bufferedReader().readLine() ?: "[]"
            Log.d(TAG, "getUsers: $nameJson")
            var names = gson.fromJson<List<String>>(nameJson, List::class.java)
            return names
        }catch (e: Exception){
            Log.e(TAG, "getUsers: $e", )
            return emptyList()
        }
    }

    companion object {
        private const val FILE_NAMES = "Name List.txt"
        private const val TAG = "TmpViewModel"
    }
}