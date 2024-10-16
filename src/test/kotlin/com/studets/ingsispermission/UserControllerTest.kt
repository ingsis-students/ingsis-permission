package com.studets.ingsispermission

import com.studets.ingsispermission.controllers.UserController
import com.studets.ingsispermission.entities.Snippet
import com.studets.ingsispermission.entities.User
import com.studets.ingsispermission.entities.request_types.CheckRequest
import com.studets.ingsispermission.entities.request_types.UserSnippet
import com.studets.ingsispermission.repositories.UserRepository
import com.studets.ingsispermission.repositories.UserSnippetsRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import java.util.Optional
import kotlin.test.assertFailsWith

@SpringBootTest()
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private lateinit var userController: UserController

    @MockBean
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var userSnippetsRepository: UserSnippetsRepository

    @BeforeEach
    fun setup() {
        val users = UserFixture.all()
        val user = User(id = 1, email = "mati@example.com", auth0Id = "auth0-123", snippets = emptyList())
        userRepository.saveAll(users)

        whenever(userRepository.findById(1)).thenAnswer { Optional.of(UserFixture.MATI_CHIALVA) }
        whenever(userRepository.findByEmail("mati@example.com")).thenAnswer { user }
        whenever(userRepository.findByAuth0Id("auth0-122")).thenAnswer { UserFixture.MATI_CHIALVA }
        whenever(userRepository.save(any<User>())).thenAnswer { UserFixture.MATI_CHIALVA }
        whenever(userRepository.findAll()).thenAnswer { users }
        whenever(userSnippetsRepository.findByUserId(anyLong())).thenAnswer {
            listOf(Snippet(id = 1, user = user, snippetId = 2, role = "Owner"))
        }
    }

    @Test
    fun `can get user by email`() {
        val user = userController.getUserByEmail("mati@example.com")
        assertNotNull(user, "User should not be null")
    }

    @Test
    fun `can get user by auth0id`() {
        val user = userRepository.findByAuth0Id("auth0-122")
        assertNotNull(user, "User should not be null")
    }

    @Test
    fun `fails to retrieve non existent user`() {
        val user = userRepository.findByAuth0Id("10")
        assertTrue(user == null, "User should be null")
    }

    @Test
    fun `can create user`() {
        val user = User(email = "nacho@example.com", auth0Id = "auth0-321")
        val response = userController.createUser(user)

        assertNotNull(response.body?.email, "User email should not be null")
    }

    @Test
    fun `find all should return all users`() {
        val users = userController.getAllUsers().body!!
        assertEquals(8, users.size)
        assertTrue(users.any { it.email == "mati@example.com" })
        assertTrue(users.any { it.email == "juliocesar@gmail.com" })
    }

    @Test
    fun `can update user`() {
        val user = User(id = 1, email = "mati@gmail.com", auth0Id = "auth0-123")
        val response = userController.updateUser(user)
        assertTrue(response.body?.email == "mati@gmail.com")
    }

    @Test
    fun `can add snippet to user`() {
        val response = userController.addSnippetToUser("mati@example.com", UserSnippet(1, "admin"))
        assertTrue(response.body == "Snippet added to user")
    }

    @Test
    fun `can check if user is owner of snippet`() {
        val response = userController.checkIfOwner(CheckRequest(2, "mati@example.com"))
        assertTrue(response.body == "User is the owner of the snippet")
    }

    @Test
    fun `find user non-existent throws exception`() {
        val exception = assertFailsWith<Exception> {
            userController.getUserByEmail("nonexistent@gmail.com")
        }
        assert(exception.message?.contains("User not found when trying to get by email") == true)
    }
}