package jp.aoyama.mki.thermometer.infrastructure.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.domain.models.user.User
import jp.aoyama.mki.thermometer.domain.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*

class UserCSVUtil {
    data class UserEntity(
        val name: String,
        val address: String,
        val grade: String,
    ) {
        fun toUser(): User {
            val userId = UUID.randomUUID().toString()
            return User(
                id = userId,
                name = name,
                devices = listOf(Device(userId = userId, address = address)),
                grade = Grade.fromGradeName(grade)
            )
        }
    }

    /**
     * 以下の形式のCSVファイルから、[UserEntity]を抽出する
     * Name, Bluetooth Mac Address, Grade
     */
    fun importFromCsv(context: Context, uri: Uri): List<User> {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return emptyList()

        return inputStream.bufferedReader().useLines { lines ->
            lines.toList().mapNotNull { line ->
                createUserFrom(line)?.toUser()
            }
        }
    }

    /**
     * 端末内のデータを、以下の形式のCSVファイルに出力する。
     * Name, Bluetooth Mac Address, Grade
     */
    suspend fun exportToCsv(context: Context): Uri = withContext(Dispatchers.IO) {
        val file = File(
            context.getExternalFilesDir(null),
            context.getString(R.string.export_file_name_user)
        )
        val outputStream = FileOutputStream(file, false)

        val service = UserService(context)
        val users = service.getUsers()

        OutputStreamWriter(outputStream).use { writer ->
            users.forEach { user ->
                val device =
                    if (user.devices.isNotEmpty()) user.devices.first().address
                    else ""
                val grade = user.grade?.gradeName ?: ""

                val csv = arrayOf(user.name, device, grade).joinToString()
                writer.append("$csv\n")
            }
        }

        return@withContext FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    private fun createUserFrom(value: String): UserEntity? {
        val elements = value
            .replace("\\s".toRegex(), "") // 空白を削除
            .split(',') // 要素を分割

        if (elements.size != 3) return null

        val name = elements[0]
        val address = elements[1]
        val grade = elements[2]

        return UserEntity(
            name = name,
            address = address,
            grade = grade
        )
    }
}