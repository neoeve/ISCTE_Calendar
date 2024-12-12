package com.example.isctecalendar.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.isctecalendar.R
import com.example.isctecalendar.data.ScheduleItem

class ScheduleAdapter(
    private val scheduleList: List<ScheduleItem>,
    private val onAttendanceAction: (scheduleId: Int, isAttending: Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_DETAIL = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (scheduleList[position]) {
            is ScheduleItem.Header -> VIEW_TYPE_HEADER
            is ScheduleItem.ScheduleDetail -> VIEW_TYPE_DETAIL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule_detail, parent, false)
            ScheduleDetailViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = scheduleList[position]) {
            is ScheduleItem.Header -> (holder as HeaderViewHolder).bind(item)
            is ScheduleItem.ScheduleDetail -> (holder as ScheduleDetailViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = scheduleList.size

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val headerTextView: TextView = view.findViewById(R.id.scheduleHeader)

        fun bind(header: ScheduleItem.Header) {
            headerTextView.text = header.date // O conteúdo será formatado (mês ou dia) na MainActivity
        }
    }

    inner class ScheduleDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val subjectTextView: TextView = view.findViewById(R.id.scheduleSubject)
        private val timeTextView: TextView = view.findViewById(R.id.scheduleTime)
        private val classRoomTextView: TextView = view.findViewById(R.id.scheduleClassRoom)

        fun bind(detail: ScheduleItem.ScheduleDetail) {
            subjectTextView.text = detail.subject ?: "Matéria não disponível"
            timeTextView.text = "${detail.startTime} - ${detail.endTime}"
            classRoomTextView.text = detail.classRoom ?: "Sala não disponível"

            // Adiciona o clique no item para abrir o dialog
            itemView.setOnClickListener {
                showAttendanceDialog(itemView.context, detail.id)
            }
        }

        private fun showAttendanceDialog(context: Context, scheduleId: Int) {
            val dialog = AlertDialog.Builder(context)
                .setTitle("Vai estar presente?")
                .setMessage("Deseja marcar presença nesta aula?")
                .setPositiveButton("Sim") { _, _ ->
                    onAttendanceAction(scheduleId, true) // Chama a função para marcar presença
                }
                .setNegativeButton("Não") { _, _ ->
                    onAttendanceAction(scheduleId, false) // Chama a função para marcar ausência
                }
                .create()

            dialog.show()
        }
    }
}
