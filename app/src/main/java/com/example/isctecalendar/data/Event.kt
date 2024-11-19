package com.example.isctecalendar.data

data class Event(
    val title: String,
    val description: String,
    val startDate: String,
    val startTime: String,
    val endTime: String, // Novo campo para a hora final
    val location: String
)
