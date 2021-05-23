package jp.aoyama.mki.thermometer.domain.models

enum class Grade(val gradeName: String) {
    B4("B4"),

    M1("M1"),

    M2("M2"),

    GRAD("Grad"),

    PROF("Prof"), ;

    companion object {
        fun fromGradeName(gradeName: String): Grade? {
            return values().firstOrNull { it.gradeName == gradeName }
        }
    }
}