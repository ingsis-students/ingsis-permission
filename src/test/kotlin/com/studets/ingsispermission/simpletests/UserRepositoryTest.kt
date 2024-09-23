package com.studets.ingsispermission.simpletests

import com.studets.ingsispermission.entities.User
import com.studets.ingsispermission.repositories.UserRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    //@Transactional to rollback changes
    fun `add user to db`() {
        val user = User(email = "mati@example.com", auth0Id = "123")
        val savedUser = userRepository.save(user)
        assertNotNull(savedUser.id, "User ID should not be null after saving")

        // Fetch the user from the database to verify it was inserted correctly
        val retrievedUser = userRepository.findByEmail("mati@example.com")
        assertNotNull(retrievedUser, "User should be found in the database")
        assertNotNull(retrievedUser?.id, "User ID should be populated")
    }
}
