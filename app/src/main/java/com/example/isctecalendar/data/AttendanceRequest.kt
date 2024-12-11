package com.example.isctecalendar.data

data class AttendanceRequest(
    val scheduleId: Int,
    val userId: Int,
    val isAttending: Boolean
)
