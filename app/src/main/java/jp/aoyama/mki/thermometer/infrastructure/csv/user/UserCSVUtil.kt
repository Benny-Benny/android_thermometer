package jp.aoyama.mki.thermometer.infrastructure.csv.user

import android.content.Context
import android.net.Uri
import jp.aoyama.mki.thermometer.domain.models.UserEntity
import java.util.*

class UserCSVUtil {

    /**
     * 以下の形式のCSVファイルから、[UserEntity]を抽出する
     * Name, Bluetooth Mac Address, Grade
     */
    suspend fun importFromCsv(context: Context, uri: Uri): List<UserEntity> {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return emptyList()

        return inputStream.bufferedReader().useLines { lines ->
            lines.toList().mapNotNull { line ->
                createUserFrom(line)
            }
        }
    }

    private fun createUserFrom(value: String): UserEntity? {
        val elements = value
            .replace("\\s".toRegex(), "") // 空白を削除
            .split(',') // 要素を分割

        if (elements.size != 3) return null

        val name = elements[0]
        val grade = elements[2]

        return UserEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            grade = grade
        )
    }
}