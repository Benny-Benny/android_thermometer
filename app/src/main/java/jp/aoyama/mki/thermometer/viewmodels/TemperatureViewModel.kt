package jp.aoyama.mki.thermometer.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.aoyama.mki.thermometer.models.TemperatureData
import jp.aoyama.mki.thermometer.models.User
import jp.aoyama.mki.thermometer.repository.LocalFileUserRepository
import jp.aoyama.mki.thermometer.util.CSVFileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TemperatureViewModel : ViewModel() {

    private val mCsvFileManager = CSVFileManager()
    private val _users: MutableLiveData<List<User>> = MutableLiveData(mutableListOf())


    fun getUsers(context: Context): LiveData<List<User>> {
        val userRepository = LocalFileUserRepository(context)

        viewModelScope.launch(Dispatchers.IO) {
            val users = userRepository.findAll()

            withContext(Dispatchers.Main) {
                _users.value = users
            }
        }

        return _users
    }

    suspend fun deleteUser(context: Context, userId: String) {
        val userRepository = LocalFileUserRepository(context)

        withContext(Dispatchers.IO) {
            userRepository.delete(userId)
        }

        _users.value = userRepository.findAll()
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
}