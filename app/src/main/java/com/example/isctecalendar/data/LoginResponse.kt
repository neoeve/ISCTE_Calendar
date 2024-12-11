package com.example.isctecalendar.data

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val student: Student?
)