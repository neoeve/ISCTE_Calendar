package com.example.isctecalendar.data

data class Schedule(
    val id: Int,
    val date: String, // Formato já adaptado para yyyy-MM-dd
    val startTime: String,
    val endTime: String,
    val classRoom: String?,
    val subject: String?
)
