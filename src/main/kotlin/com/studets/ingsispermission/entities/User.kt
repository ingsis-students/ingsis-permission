package com.studets.ingsispermission.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.persistence.OneToMany

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = true, unique = true)
    val auth0Id: String,

    @OneToMany(mappedBy = "user")
    val snippets: List<Snippet> = emptyList()

) {
    constructor() : this(null, "@", "nonexistent")

    override fun toString(): String {
        return "User(id=$id, email='$email', auth0Id='$auth0Id')"
    }
}

data class CreateUser(
    val email: String
)
