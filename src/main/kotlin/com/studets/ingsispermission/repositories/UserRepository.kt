package com.studets.ingsispermission.repositories

import com.studets.ingsispermission.entities.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> { // implements jpa basic functionality.
    fun findByEmail(email: String): User?
    fun findByAuth0Id(auth0Id: String): User?
}
