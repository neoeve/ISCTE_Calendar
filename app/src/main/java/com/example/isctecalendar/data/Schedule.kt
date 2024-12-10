package com.example.isctecalendar.data

data class Schedule(
    val id: Int,
    val discipline: String,
    val date: String,
    val time_start: String,
    val time_end: String,
    val room: String
)
