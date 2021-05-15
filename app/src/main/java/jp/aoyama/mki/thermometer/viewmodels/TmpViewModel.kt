package jp.aoyama.mki.thermometer.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import jp.aoyama.mki.thermometer.models.TemperatureData
import jp.aoyama.mki.thermometer.util.CSVFileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TmpViewModel : ViewModel() {

    private val mCsvFileManager = CSVFileManager()

    fun getUsers(context: Context): List<String> {
        return try {
            val nameJson = context.openFileInput(FILE_NAMES).bufferedReader().readLine() ?: "[]"
            val names = Gson().fromJson<List<String>>(nameJson, List::class.java)
            names
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addUser(context: Context, name: String) {
        val currentUsers = getUsers(context).toMutableList()
        currentUsers.add(name)

        val namesJson: String = Gson().toJson(currentUsers)

        context.openFileOutput(FILE_NAMES, Context.MODE_PRIVATE).use {
            it.write(namesJson.toByteArray())
        }
    }

    fun deleteUser(context: Context, name: String) {
        val currentUsers = getUsers(context).toMutableList()
        currentUsers.removeAll { nameCurrent -> nameCurrent == name }

        val namesJson: String = Gson().toJson(currentUsers)

        context.openFileOutput(FILE_NAMES, Context.MODE_PRIVATE).use {
            it.write(namesJson.toByteArray())
        }
    }

    /**
     * 入力された体温をCSV形式で保存
     * @param name 体温を記録する人の名前
     * @param temperature 体温
     * @return 入力値が適切であれば true を返す
     */
    fun saveTemperature(context: Context, name: String, temperature: String?): Boolean {
        val inputValue = temperature?.toFloatOrNull() ?: return false
        if (inputValue > 45f || inputValue < 35f) return false

        // 内部のＣＳＶファイルにデータを追加
        viewModelScope.launch(Dispatchers.IO) {
            mCsvFileManager.append(context, TemperatureData(name = name, temperature = inputValue))
        }

        return true
    }

    companion object {
        private const val FILE_NAMES = "Name List.txt"
        private const val TAG = "TmpViewModel"
    }
}