// src/main/kotlin/com/library/system/repository/BorrowingRecordRepository.kt
package com.library.system.repository

import com.library.system.model.BorrowingRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface BorrowingRecordRepository : JpaRepository<BorrowingRecord, UUID> {
    fun findByBookIdAndReturnDateIsNull(bookId: UUID): Optional<BorrowingRecord>

    fun countByUserIdAndReturnDateIsNull(userId: UUID): Long
}
