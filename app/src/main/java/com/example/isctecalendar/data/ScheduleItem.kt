package com.example.isctecalendar.data

data class ScheduleItem(
    val id: Int,
    val day: String,
    val startTime: String,
    val endTime: String,
    val subject: String,
    val classroom: String
)
