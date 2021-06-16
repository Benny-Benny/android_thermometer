package jp.aoyama.mki.thermometer.infrastructure.local.user

import android.content.Context
import android.net.Uri
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.domain.models.user.User
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
    suspend fun importFromCsv(context: Context, uri: Uri): List<User> {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return emptyList()

        return inputStream.bufferedReader().useLines { lines ->
            lines.toList().mapNotNull { line ->
                createUserFrom(line)?.toUser()
            }
        }
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