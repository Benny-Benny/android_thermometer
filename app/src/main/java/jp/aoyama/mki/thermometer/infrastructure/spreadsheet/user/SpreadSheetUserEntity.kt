package jp.aoyama.mki.thermometer.infrastructure.spreadsheet.user

import jp.aoyama.mki.thermometer.domain.models.user.UserEntity

data class SpreadSheetUserEntity(
    val id: String,
    val name: String,
    val grade: String? = null
) {
    fun toCsv(): List<String> {
        return listOf(
            this.id,
            this.name,
            this.grade ?: ""
        )
    }

    fun toEntity(): UserEntity {
        return UserEntity(id, name, grade)
    }

    companion object {
        fun fromCSV(csv: List<String>): SpreadSheetUserEntity? {
            if (csv.size < 2) return null
            return SpreadSheetUserEntity(
                id = csv[0],
                name = csv[1],
                grade = if (csv.size < 3) "" else csv[2]
            )
        }


        fun fromUserEntity(user: UserEntity): SpreadSheetUserEntity {
            return SpreadSheetUserEntity(
                id = user.id,
                name = user.name,
                grade = user.grade,
            )
        }
    }
}