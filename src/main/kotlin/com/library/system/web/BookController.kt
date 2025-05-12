// src/main/kotlin/com/library/system/controllers/BookController.kt
package com.library.system.web

import com.library.system.model.*
import com.library.system.services.BookService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/books")
@Component("webBookController")
class BookController(private val bookService: BookService) {

    @PostMapping
    fun addBook(@RequestBody book: Book): ResponseEntity<Book> {
        val savedBook = bookService.addBook(book)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook)
    }

    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: UUID): ResponseEntity<Book> {
        return try {
            val book = bookService.getBookById(id)
            ResponseEntity.ok(book)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping
    fun getAllBooks(): ResponseEntity<List<Book>> {
        val books = bookService.getAllBooks()
        return ResponseEntity.ok(books)
    }

    @PutMapping("/{id}")
    fun updateBook(@PathVariable id: UUID, @RequestBody bookDetails: Book): ResponseEntity<Book> {
        return try {
            val updatedBook = bookService.updateBook(id, bookDetails)
            ResponseEntity.ok(updatedBook)
        } catch (e: Exception) {
            return ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteBookById(@PathVariable id: UUID): ResponseEntity<Void> {
        return try {
            bookService.deleteBookById(id)
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/search")
    fun searchBooks(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) author: String?,
        @RequestParam(required = false) available: String?
    ): ResponseEntity<List<Book>> {
        val availableBoolean = available?.toBooleanStrictOrNull()
        val books = bookService.searchBooks(title, author, availableBoolean)
        return ResponseEntity.ok(books)
    }

    @PostMapping("/{bookId}/borrow")
    fun borrowBook(@PathVariable bookId: UUID, @RequestParam userId: String): ResponseEntity<Book> {
        return try {
            val userIdUUID = UUID.fromString(userId)
            val borrowedBook = bookService.borrowBook(bookId, userIdUUID)
            ResponseEntity.ok(borrowedBook)
        } catch (e: Exception) {
            if (e is ResourceNotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            } else if (e is BookNotAvailableException) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build()
            } else if (e is BorrowingLimitExceededException) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build()
            }
            ResponseEntity.internalServerError().build()
        }
    }

    @PostMapping("/{bookId}/return")
    fun returnBook(@PathVariable bookId: UUID): ResponseEntity<Book> {
        return try {
            val returnedBook = bookService.returnBook(bookId)
            ResponseEntity.ok(returnedBook)
        } catch (e: Exception) {
            if (e is ResourceNotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            } else if (e is BookAlreadyReturnedException) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build()
            }
            ResponseEntity.internalServerError().build()
        }
    }
}