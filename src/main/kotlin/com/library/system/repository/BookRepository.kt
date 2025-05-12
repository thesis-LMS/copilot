// src/main/kotlin/com/library/system/repository/BookRepository.kt
package com.library.system.repository

import com.library.system.model.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BookRepository : JpaRepository<Book, UUID> {
    fun findByTitleContainingIgnoreCase(title: String): List<Book>
    fun findByAuthorContainingIgnoreCase(author: String): List<Book>
    fun findByAvailable(available: Boolean): List<Book>
    fun findByTitleContainingIgnoreCaseAndAvailable(title: String, available: Boolean): List<Book>
    fun findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndAvailable(title: String, author: String, available: Boolean): List<Book>
}