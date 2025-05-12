// src/main/kotlin/com/library/system/controllers/UserController.kt
package com.library.system.web

import com.library.system.model.User
import com.library.system.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun registerUser(@RequestBody user: User): ResponseEntity<User> {
        val newUser = userService.registerUser(user)
        return ResponseEntity(newUser, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<User> {
        return try {
            val user = userService.getUserById(id)
            ResponseEntity.ok(user)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody userDetails: User): ResponseEntity<User> {
        return try {
            val updatedUser = userService.updateUser(id, userDetails)
            ResponseEntity.ok(updatedUser)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }
}