// src/main/kotlin/com/library/system/model/Book.kt
package com.library.system.model

import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "books")
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @Column(nullable = false)
    var title: String,
    @Column(nullable = false)
    var author: String,
    var available: Boolean = true,
    var borrowedByUserId: UUID? = null,
    var dueDate: LocalDate? = null,
)
