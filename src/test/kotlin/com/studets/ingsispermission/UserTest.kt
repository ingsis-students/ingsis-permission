package com.studets.ingsispermission

import com.studets.ingsispermission.controllers.UserController
import com.studets.ingsispermission.entities.Author
import com.studets.ingsispermission.entities.Snippet
import com.studets.ingsispermission.repositories.UserRepository
import com.studets.ingsispermission.repositories.UserSnippetsRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles

@SpringBootTest()
@ActiveProfiles("test")
class UserTest {
    @Autowired
    private lateinit var userController: UserController

    @Autowired
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var userSnippetsRepository: UserSnippetsRepository

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
        val users = UserFixture.all()
        userRepository.saveAll(users)

        val user = Author(id = 1, email = "mati@example.com", auth0Id = "auth0-123", snippets = emptyList())
        whenever(userSnippetsRepository.findByAuthorId(anyLong())).thenReturn(
            listOf(
                Snippet(
                    id = 1, author = user, snippetId = 2, role = "Owner"
                )
            )
        )
    }

    @BeforeEach
    fun printDatabaseContents() {
        val users = userRepository.findAll()
        println("Current users in database:")
        users.forEach {
            println("User ID: ${it.id}, Email: ${it.email}, Auth0 ID: ${it.auth0Id}")
        }
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_read:snippets"])
    fun `can get user by email`() {
        val user = userController.getUserByEmail("mati@example.com")
        println("user: $user")
        assertNotNull(user.body, "User should not be null")
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_read:snippets"])
    fun `can get user by id`() {
        val user = userController.getUserByEmail("mati@example.com")
        val user2 = userController.getUserById(user.body?.id!!)
        assertEquals(user.body?.id!!, user2.body?.id!!, "User should be the same")
    }
}
