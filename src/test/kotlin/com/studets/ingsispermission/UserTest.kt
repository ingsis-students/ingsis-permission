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

    @Test
    @WithMockUser(authorities = ["SCOPE_read:snippets"])
    fun `should return snippets by user id`() {
        val user = userRepository.findByEmail("mati@example.com")
        val response = userController.getUserSnippetsId(user!!.id!!)

        assertNotNull(response.body, "Snippet IDs should not be null")
        assertEquals(1, response.body!!.size, "There should be one snippet ID")
        assertEquals(1, response.body!![0], "Snippet ID should match expected value")
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_read:snippets"])
    fun `should return snippets of user by auth id`() {
        val auth0Id = "auth0-123"
        val userSnippets = userController.getUserSnippets(auth0Id)
        assertNotNull(userSnippets.body, "User snippets should not be null")
        assertEquals(1, userSnippets.body!!.size, "User should have one snippet")
        assertEquals(2, userSnippets.body!![0].snippetId, "Snippet ID should match expected value")
    }
}
