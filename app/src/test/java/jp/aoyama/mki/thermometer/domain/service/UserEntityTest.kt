package jp.aoyama.mki.thermometer.domain.service

import jp.aoyama.mki.thermometer.domain.models.user.User
import jp.aoyama.mki.thermometer.view.models.UserEntity
import jp.aoyama.mki.thermometer.view.models.UserEntity.Companion.updateUser
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

internal class UserEntityTest {
    private val user = User(name = "test", device = null)

    @Test
    fun `最後に発見されたのが10分前の場合 found=False`() {
        val now = Calendar.getInstance().timeInMillis
        val user1 = UserEntity(user, lastFoundAt = now)

        val tenMinutesAgo = now - 10 * 60 * 1000
        val user2 = UserEntity(user, lastFoundAt = tenMinutesAgo)

        assertTrue(user1.found)
        assertFalse(user2.found)
    }

    @Test
    fun `ユーザーの発見状態を更新`() {
        val now = Calendar.getInstance()
        val tenMinutesAgo = now.timeInMillis - 10 * 60 * 1000

        val user1 = UserEntity(user, lastFoundAt = tenMinutesAgo)
        val user2 = UserEntity(user, lastFoundAt = null)
        assertFalse(user1.found)
        assertFalse(user2.found)

        var users = listOf(user1, user2)
        users = users.updateUser(user1.copy(lastFoundAt = now))
        users = users.updateUser(user2.copy(lastFoundAt = now))

        val updatedUser1 = users.find { it.id == user1.id }!!
        val updatedUser2 = users.find { it.id == user2.id }!!
        assertTrue(updatedUser1.found)
        assertTrue(updatedUser2.found)
    }
}