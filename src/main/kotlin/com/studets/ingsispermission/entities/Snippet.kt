package com.studets.ingsispermission.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.ManyToOne

@Entity
@Table(name = "user_snippets")
data class Snippet(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @ManyToOne
    val author: Author,

    @Column(nullable = false)
    val snippetId: Long,

    @Column(nullable = false)
    val role: String
) {
    constructor() : this(0, Author(), 0, "nonexistent")

    override fun toString(): String {
        return "Snippet(snippetId=$snippetId, user=${author.email}, role='$role')"
    }
}
