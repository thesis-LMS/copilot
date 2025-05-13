// src/main/kotlin/com/library/system/model/User.kt
package com.library.system.model

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false, unique = true)
    var email: String,
    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.MEMBER,
)

enum class UserRole {
    MEMBER,
    LIBRARIAN,
}
