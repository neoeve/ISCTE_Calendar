package com.example.isctecalendar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.isctecalendar.R
import com.example.isctecalendar.data.Event
import com.example.isctecalendar.data.EventsByDay

class EventsAdapter(
    private val eventsByDay: List<EventsByDay>,
    private val onEventClick: (Event) -> Unit // Callback para cliques nos eventos
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return eventsByDay.sumOf { it.events.size + 1 } // +1 para o cabeçalho
    }

    override fun getItemViewType(position: Int): Int {
        var offset = 0
        for (day in eventsByDay) {
            if (position == offset) return 0 // Cabeçalho
            if (position < offset + day.events.size + 1) return 1 // Item
            offset += day.events.size + 1
        }
        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
            EventViewHolder(view, onEventClick)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var offset = 0
        for (day in eventsByDay) {
            if (position == offset) {
                (holder as HeaderViewHolder).bind(day.date)
                return
            }
            if (position < offset + day.events.size + 1) {
                val event = day.events[position - offset - 1]
                (holder as EventViewHolder).bind(event)
                return
            }
            offset += day.events.size + 1
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val headerTextView: TextView = view.findViewById(R.id.headerTextView)
        fun bind(date: String) {
            headerTextView.text = date
        }
    }

    class EventViewHolder(
        view: View,
        private val onEventClick: (Event) -> Unit // Callback para cliques
    ) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.eventTitleTextView)
        private val timeTextView: TextView = view.findViewById(R.id.eventTimeTextView)
        fun bind(event: Event) {
            titleTextView.text = event.title
            timeTextView.text = event.startTime
            itemView.setOnClickListener {
                onEventClick(event) // Chama o callback ao clicar no evento
            }
        }
    }
}
