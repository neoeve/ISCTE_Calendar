package com.example.isctecalendar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.isctecalendar.R
import com.example.isctecalendar.data.Schedule

class ScheduleAdapter(
    private val scheduleList: List<Schedule>,
    private val onAttendanceChange: (scheduleId: Int, isAttending: Boolean) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = scheduleList[position]

        holder.dateTextView.text = schedule.date
        holder.subjectTextView.text = schedule.subject ?: "Matéria não disponível"
        holder.timeTextView.text = "${schedule.startTime.substring(0, 5)} - ${schedule.endTime.substring(0, 5)}"
        holder.classRoomTextView.text = schedule.classRoom ?: "Sala não disponível"

        // Listener para presença
        holder.attendButton.setOnClickListener {
            onAttendanceChange(schedule.id, true)
        }

        // Listener para ausência
        holder.notAttendButton.setOnClickListener {
            onAttendanceChange(schedule.id, false)
        }
    }

    override fun getItemCount(): Int = scheduleList.size

    inner class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.scheduleDate)
        val subjectTextView: TextView = view.findViewById(R.id.scheduleSubject)
        val timeTextView: TextView = view.findViewById(R.id.scheduleTime)
        val classRoomTextView: TextView = view.findViewById(R.id.scheduleClassRoom)
        val attendButton: Button = view.findViewById(R.id.attendButton)
        val notAttendButton: Button = view.findViewById(R.id.notAttendButton)
    }
}