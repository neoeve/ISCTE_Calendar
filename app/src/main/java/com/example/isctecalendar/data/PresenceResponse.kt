package com.example.isctecalendar.data

data class PresenceResponse(
    val success: Boolean,
    val message: String,
    val scheduleId: Int? = null
)
