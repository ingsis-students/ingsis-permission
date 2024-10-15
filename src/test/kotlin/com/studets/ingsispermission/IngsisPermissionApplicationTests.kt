package com.studets.ingsispermission

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test") // use test database - h2
class IngsisPermissionApplicationTests {

    @Test
    fun contextLoads() {
    }
}
