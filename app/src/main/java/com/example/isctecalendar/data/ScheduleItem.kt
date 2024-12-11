package com.example.isctecalendar.data

sealed class ScheduleItem {
    data class Header(val date: String) : ScheduleItem()
    data class ScheduleDetail(
        val id: Int,
        val startTime: String,
        val endTime: String,
        val subject: String?,
        val classRoom: String?
    ) : ScheduleItem()
}
