// src/main/kotlin/com/library/system/services/BookService.kt
package com.library.system.services

import com.library.system.model.*
import com.library.system.repository.BookRepository
import com.library.system.repository.BorrowingRecordRepository
import com.library.system.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class BookService @Autowired constructor(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val borrowingRecordRepository: BorrowingRecordRepository
) {
    @Value("\${library.borrowing-limit:5}")
    private var borrowingLimit: Long = 5

    fun addBook(book: Book): Book {
        return bookRepository.save(book)
    }

    fun getBookById(id: UUID): Book {
        return bookRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Book with ID $id not found") }
    }

    fun getAllBooks(): List<Book> {
        return bookRepository.findAll()
    }

    fun updateBook(id: UUID, bookDetails: Book): Book {
        return bookRepository.findById(id)
            .map { existingBook ->
                val updatedBook = existingBook.copy(
                    title = bookDetails.title,
                    author = bookDetails.author,
                    available = bookDetails.available
                )
                bookRepository.save(updatedBook)
            }
            .orElseThrow { ResourceNotFoundException("Book with ID $id not found") }
    }

    fun deleteBookById(id: UUID) {
        if (!bookRepository.existsById(id)) {
            throw ResourceNotFoundException("Book with ID $id not found for deletion")
        }
        bookRepository.deleteById(id)
    }

    fun searchBooks(title: String?, author: String?, available: Boolean?): List<Book> {
        if (title != null && author != null && available != null) {
            return bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndAvailable(title, author, available)
        } else if (title != null && author == null && available == null) {
            return bookRepository.findByTitleContainingIgnoreCase(title)
        } else if (title == null && author != null && available == null) {
            return bookRepository.findByAuthorContainingIgnoreCase(author)
        } else if (title == null && author == null && available != null) {
            return bookRepository.findByAvailable(available)
        } else if (title != null && available != null) {
            return bookRepository.findByTitleContainingIgnoreCaseAndAvailable(title, available)
        }

        return emptyList()
    }

    fun borrowBook(bookId: UUID, userId: UUID): Book {
        val book = bookRepository.findById(bookId)
            .orElseThrow { ResourceNotFoundException("Book with ID $bookId not found") }

        if (!book.available) {
            throw BookNotAvailableException("Book with ID $bookId is not available for borrowing.")
        }

        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User with ID $userId not found for borrowing.") }

        val currentBorrowedBooksCount = borrowingRecordRepository.countByUserIdAndReturnDateIsNull(userId)
        if (currentBorrowedBooksCount >= borrowingLimit) {
            throw BorrowingLimitExceededException("User with ID $userId has reached the borrowing limit of $borrowingLimit books.")
        }

        // Create the due date
        val dueDate = LocalDate.now().plusWeeks(2)

        // Create and save the borrowing record
        val borrowingRecord = BorrowingRecord(
            bookId = bookId,
            userId = userId,
            borrowDate = LocalDate.now(),
            dueDate = dueDate,
            returnDate = null,
            lateFee = 0.0
        )
        borrowingRecordRepository.save(borrowingRecord)

        val borrowedBook = book.copy(available = false, borrowedByUserId = userId, dueDate = dueDate)
        return bookRepository.save(borrowedBook)
    }

    fun returnBook(bookId: UUID): Book {
        val book = bookRepository.findById(bookId)
            .orElseThrow { ResourceNotFoundException("Book with ID $bookId not found") }

        val borrowingRecord = borrowingRecordRepository.findByBookIdAndReturnDateIsNull(bookId)
            .orElseThrow { BookAlreadyReturnedException("Book with ID $bookId is already available or no active borrowing record found.") }

        val returnDate = LocalDate.now()
        val daysOverdue = if (returnDate.isAfter(borrowingRecord.dueDate)) {
            java.time.temporal.ChronoUnit.DAYS.between(borrowingRecord.dueDate, returnDate)
        } else {
            0
        }
        val lateFee = daysOverdue * 0.5

        borrowingRecord.returnDate = returnDate
        borrowingRecord.lateFee = lateFee
        borrowingRecordRepository.save(borrowingRecord)

        val returnedBook = book.copy(available = true, borrowedByUserId = null, dueDate = null)
        return bookRepository.save(returnedBook)
    }
}