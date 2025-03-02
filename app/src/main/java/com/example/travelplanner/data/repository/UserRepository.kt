package com.example.travelplanner.data.repository

object UserRepository {
    private val users = mapOf(
        "admin" to "password123",
        "user1" to "mypassword",
        "user2" to "securepass"
    )

    fun validateUser(userId: String, password: String): Boolean {
        return users[userId] == password
    }
}
