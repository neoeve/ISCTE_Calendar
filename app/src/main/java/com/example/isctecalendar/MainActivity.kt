package com.example.isctecalendar

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.isctecalendar.network.ApiService
import com.example.isctecalendar.network.RetrofitClient
import com.example.isctecalendar.data.Schedule
import com.example.isctecalendar.adapters.ScheduleAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtém a turma passada pela LoginActivity ou RegisterActivity
        val turma = intent.getStringExtra("turma") ?: ""

        if (turma.isNotEmpty()) {
            fetchSchedule(turma)
        } else {
            Toast.makeText(this, "Erro: Turma não encontrada.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchSchedule(turma: String) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        apiService.getSchedule(turma).enqueue(object : Callback<List<Schedule>> {
            override fun onResponse(call: Call<List<Schedule>>, response: Response<List<Schedule>>) {
                if (response.isSuccessful) {
                    val scheduleList = response.body()
                    if (!scheduleList.isNullOrEmpty()) {
                        setupRecyclerView(scheduleList)
                    } else {
                        Toast.makeText(this@MainActivity, "Nenhum horário encontrado para a turma.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Erro ao buscar horário.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Schedule>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Erro de rede: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView(scheduleList: List<Schedule>) {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ScheduleAdapter(scheduleList)
    }
}
