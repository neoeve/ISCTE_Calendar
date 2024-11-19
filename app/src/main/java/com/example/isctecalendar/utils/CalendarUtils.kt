package com.example.isctecalendar.utils

import android.util.Log
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Summary
import net.fortuna.ical4j.model.property.Description
import net.fortuna.ical4j.model.property.DtStart
import net.fortuna.ical4j.model.property.DtEnd
import net.fortuna.ical4j.model.property.Location
import okhttp3.OkHttpClient
import okhttp3.Request
import com.example.isctecalendar.data.Event
import com.example.isctecalendar.data.EventsByDay
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.*

object CalendarUtils {
    fun fetchCalendarEventsFromUrl(url: String): List<EventsByDay> {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            Log.d("Calendar", "Dados recebidos: $responseBody")
            parseICalData(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Calendar", "Erro ao buscar eventos: ${e.message}")
            emptyList()
        }
    }

    fun parseICalData(icalData: String): List<EventsByDay> {
        val events = mutableListOf<Event>()
        val builder = CalendarBuilder()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) // Data atual

        try {
            val stringReader = StringReader(icalData)
            val calendar = builder.build(stringReader)

            calendar.components.forEach { component ->
                if (component is VEvent) {
                    val summary = (component.getProperty("SUMMARY") as? Summary)?.value ?: "Sem título"
                    val description = (component.getProperty("DESCRIPTION") as? Description)?.value ?: "Sem descrição"
                    val start = (component.getProperty("DTSTART") as? DtStart)?.value ?: ""
                    val end = (component.getProperty("DTEND") as? DtEnd)?.value ?: ""
                    val location = (component.getProperty("LOCATION") as? Location)?.value ?: "Local não especificado"

                    var eventDate: String
                    var eventStartTime: String
                    var eventEndTime: String = "Hora de término não especificada" // Valor padrão para DTEND ausente

                    try {
                        val inputDateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.US)

                        // Parsing do horário de início
                        val parsedStartDate = inputDateFormat.parse(start)
                        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

                        eventDate = dateFormatter.format(parsedStartDate ?: Date())
                        eventStartTime = timeFormatter.format(parsedStartDate ?: Date())

                        // Parsing do horário de término, se disponível
                        if (end.isNotEmpty()) {
                            val parsedEndDate = inputDateFormat.parse(end)
                            eventEndTime = timeFormatter.format(parsedEndDate ?: Date())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("Calendar", "Erro ao processar data: ${e.message}")
                        eventDate = "Data inválida"
                        eventStartTime = "Hora inválida"
                    }

                    // Adiciona o evento à lista, superiores à data dia
                    if (eventDate >= today) {
                        events.add(Event(summary, description, eventDate, eventStartTime, eventEndTime, location))
                        }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Calendar", "Erro ao processar eventos: ${e.message}")
        }

        // Ordena e agrupa os eventos
        return events
            .sortedBy { it.startDate } // Ordena por data
            .groupBy { it.startDate }
            .map { EventsByDay(it.key, it.value) }
    }

}
