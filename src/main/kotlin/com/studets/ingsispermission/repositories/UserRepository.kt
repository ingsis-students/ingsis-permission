package com.studets.ingsispermission.repositories

import com.studets.ingsispermission.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<User, Long> { // implements jpa basic functionality.
    @Query("SELECT u FROM User u WHERE u.email = :email")
    fun findByEmail(@Param("email") email: String): User?
    fun findByAuth0Id(auth0Id: String): User?
}
