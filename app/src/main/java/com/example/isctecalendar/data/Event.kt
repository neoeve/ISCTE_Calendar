package com.example.isctecalendar.data

data class Event(
    val title: String,
    val description: String,
    val startDate: String, // Formato: yyyy-MM-dd
    val startTime: String, // Formato: HH:mm
    val location: String
)
