package com.example.isctecalendar

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.isctecalendar.adapters.EventsAdapter
import com.example.isctecalendar.fragments.EventDetailsDialogFragment
import com.example.isctecalendar.utils.CalendarUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        System.setProperty("net.fortuna.ical4j.timezone.cache.impl", "net.fortuna.ical4j.util.MapTimeZoneCache")

        // Inicializa o RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val url = "https://fenix-mais.iscte-iul.pt/api/ical/publicPersonICalendar?username=dadcl1@iscte-iul.pt&password=rzaSYT3SAo6j6uP1wOQrh337jHB35IDYZj15Tdt3MdscyXWDInXmI1Z9dX7AvdqXjiRUDvyPBMolxwwjHV8OnqAlOkwcYCYYXKCPwpe1EjAB7RSTxBI0M4wKtRu1skko"

        // Busca os eventos do calendário
        CoroutineScope(Dispatchers.IO).launch {
            val events = CalendarUtils.fetchCalendarEventsFromUrl(url)

            // Atualiza a UI na thread principal
            withContext(Dispatchers.Main) {
                if (events.isNotEmpty()) {
                    val adapter = EventsAdapter(events) { event ->
                        // Trata cliques nos eventos
                        EventDetailsDialogFragment(event).show(supportFragmentManager, "EventDetails")
                    }
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(this@MainActivity, "Nenhum evento encontrado!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
