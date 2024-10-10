package com.studets.ingsispermission.simpletests

import com.studets.ingsispermission.entities.User
import com.studets.ingsispermission.repositories.UserRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@ActiveProfiles("test") // use test database - h2
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    @Transactional // to rollback changes
    fun `add user to db`() {
        val user = User(email = "mati@example.com", auth0Id = "123")
        val savedUser = userRepository.save(user)
        assertNotNull(savedUser.id, "User ID should not be null after saving")

        // Fetch the user from the database to verify it was inserted correctly
        val retrievedUser = userRepository.findByEmail("mati@example.com")
        assertNotNull(retrievedUser, "User should be found in the database")
        assertNotNull(retrievedUser?.id, "User ID should be populated")
    }

    @Test
    @Transactional
    fun `find by email should return user`() {
        val testUser = User(email = "test@example.com", auth0Id = "auth0|123456")
        userRepository.save(testUser)

        val retrievedUser = userRepository.findByEmail("test@example.com")

        assertNotNull(retrievedUser)
        assertEquals("test@example.com", retrievedUser?.email)
    }

    @Test
    @Transactional
    fun `find all should return all users`() {
        val testUser1 = User(email = "user1@example.com", auth0Id = "auth0|123")
        val testUser2 = User(email = "user2@example.com", auth0Id = "auth0|456")
        userRepository.save(testUser1)
        userRepository.save(testUser2)
        val users = userRepository.findAll()
        assertEquals(2, users.size) // Assuming the DB is empty before the test
        assertTrue(users.any { it.email == "user1@example.com" })
        assertTrue(users.any { it.email == "user2@example.com" })
    }
}
