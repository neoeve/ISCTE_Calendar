package com.example.isctecalendar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.isctecalendar.R
import com.example.isctecalendar.data.Schedule

class ScheduleAdapter(private val scheduleList: List<Schedule>) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    // ViewHolder para representar cada item do RecyclerView
    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val disciplineTextView: TextView = itemView.findViewById(R.id.disciplineTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val roomTextView: TextView = itemView.findViewById(R.id.roomTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        // Inflar o layout para cada item da lista
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        // Preencher os dados para cada item na posição atual
        val scheduleItem = scheduleList[position]
        holder.disciplineTextView.text = scheduleItem.discipline
        holder.dateTextView.text = scheduleItem.date
        holder.timeTextView.text = "${scheduleItem.time_start} - ${scheduleItem.time_end}"
        holder.roomTextView.text = scheduleItem.room
    }

    override fun getItemCount(): Int {
        // Retorna o tamanho da lista
        return scheduleList.size
    }
}

