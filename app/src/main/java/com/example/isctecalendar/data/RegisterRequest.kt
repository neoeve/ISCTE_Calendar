package com.example.isctecalendar.data

data class RegisterRequest(
    val numero: Int,
    val email: String,
    val password: String,
    val turma: String
)
