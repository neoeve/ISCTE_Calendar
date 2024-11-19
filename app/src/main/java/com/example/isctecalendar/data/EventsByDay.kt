package com.example.isctecalendar.data

data class EventsByDay(
    val date: String, // yyyy-MM-dd
    val events: List<Event>
)