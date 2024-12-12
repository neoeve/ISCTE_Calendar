package com.example.isctecalendar.data

data class ScheduleResponse(
    val success: Boolean,
    val message: String?,
    val schedules: List<Schedule>
)