package com.example.isctecalendar.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.isctecalendar.R
import com.example.isctecalendar.data.Event

class EventDetailsDialogFragment(private val event: Event) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_event_details, null)

        // Configure os elementos do layout
        val titleTextView = view.findViewById<TextView>(R.id.eventDetailsTitleTextView)
        val timeTextView = view.findViewById<TextView>(R.id.eventDetailsTimeTextView)
        val confirmButton = view.findViewById<Button>(R.id.confirmButton)

        titleTextView.text = event.title
        timeTextView.text = "Data: ${event.startDate}\nHora: ${event.startTime}"

        // Configuração do botão de confirmação
        confirmButton.setOnClickListener {
            // Exibe uma mensagem de confirmação (ou salve a presença)
            dismiss()
        }

        builder.setView(view)
        return builder.create()
    }
}
