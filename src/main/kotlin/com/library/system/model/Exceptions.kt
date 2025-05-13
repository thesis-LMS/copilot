package com.library.system.model

import java.lang.RuntimeException

class ResourceNotFoundException(
    message: String,
) : RuntimeException(message)

class BookNotAvailableException(
    message: String,
) : RuntimeException(message)

class BookAlreadyReturnedException(
    message: String,
) : RuntimeException(message)

class BorrowingLimitExceededException(
    message: String,
) : RuntimeException(message)
