package com.studets.ingsispermission

import com.studets.ingsispermission.controllers.UserController
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest // loads spring app -> integration testing
@ActiveProfiles("test") // use test database - h2
class IngsisPermissionApplicationTests {

    @Autowired
    private lateinit var userController: UserController

    @Test
    fun contextLoads() {
    }
}
