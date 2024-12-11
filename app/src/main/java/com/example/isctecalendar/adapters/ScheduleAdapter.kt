package com.example.isctecalendar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.isctecalendar.R
import com.example.isctecalendar.data.Schedule

class ScheduleAdapter(private val scheduleList: List<Schedule>) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = scheduleList[position]

        // Exibe a data
        holder.dateTextView.text = schedule.date

        // Exibe o nome da matéria (subject)
        holder.subjectTextView.text = schedule.subject ?: "Matéria não disponível"

        // Formata as horas no estilo "18:00 - 19:30"
        val startTime = schedule.startTime.substring(0, 5) // Remove os segundos
        val endTime = schedule.endTime.substring(0, 5) // Remove os segundos
        holder.timeTextView.text = "$startTime - $endTime"

        // Exibe o nome da sala
        holder.classRoomTextView.text = schedule.classRoom ?: "Sala não disponível"
    }

    override fun getItemCount(): Int = scheduleList.size

    inner class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.scheduleDateTextView)
        val timeTextView: TextView = view.findViewById(R.id.timeTextView)
        val classRoomTextView: TextView = view.findViewById(R.id.classRoomTextView)
        val subjectTextView: TextView = view.findViewById(R.id.subjectTextView)
    }
}
