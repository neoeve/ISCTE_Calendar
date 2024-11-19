package com.example.isctecalendar.utils

import android.util.Log
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Summary
import net.fortuna.ical4j.model.property.Description
import net.fortuna.ical4j.model.property.DtStart
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

        try {
            val stringReader = StringReader(icalData)
            val calendar = builder.build(stringReader)

            calendar.components.forEach { component ->
                if (component is VEvent) {
                    val summary = (component.getProperty("SUMMARY") as? Summary)?.value ?: "Sem título"
                    val description = (component.getProperty("DESCRIPTION") as? Description)?.value ?: "Sem descrição"
                    val start = (component.getProperty("DTSTART") as? DtStart)?.value ?: ""
                    val location = (component.getProperty("LOCATION") as? Location)?.value ?: "Local não especificado"

                    var eventDate: String
                    var eventTime: String

                    try {
                        // Parsing da data no formato sem 'Z'
                        val inputDateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.US)
                        val parsedDate = inputDateFormat.parse(start)

                        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

                        eventDate = dateFormatter.format(parsedDate ?: Date())
                        eventTime = timeFormatter.format(parsedDate ?: Date())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("Calendar", "Erro ao processar data: ${e.message}")
                        eventDate = "Data inválida"
                        eventTime = "Hora inválida"
                    }

                    events.add(Event(summary, description, eventDate, eventTime, location))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Calendar", "Erro ao processar eventos: ${e.message}")
        }

        return events.groupBy { it.startDate }
            .map { EventsByDay(it.key, it.value) }
    }
}
