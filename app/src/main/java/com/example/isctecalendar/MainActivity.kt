package com.example.isctecalendar

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.isctecalendar.network.ApiService
import com.example.isctecalendar.network.RetrofitClient
import com.example.isctecalendar.data.Schedule
import com.example.isctecalendar.adapters.ScheduleAdapter
import com.example.isctecalendar.data.ScheduleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var scheduleAdapter: ScheduleAdapter
    private var scheduleList: MutableList<Schedule> = mutableListOf()
    private var classGroupId: Int = -1 // Receber o ID do grupo de turma

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        scheduleAdapter = ScheduleAdapter(scheduleList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = scheduleAdapter

        // Receber classGroupId passado do LoginActivity
        classGroupId = intent.getIntExtra("classGroupId", -1)
        if (classGroupId == -1) {
            Toast.makeText(this, "Erro: Turma não encontrada.", Toast.LENGTH_SHORT).show()
            return
        }

        // Buscar o horário para a turma
        fetchSchedule(classGroupId)
    }

    private fun fetchSchedule(classGroupId: Int) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getScheduleByClassGroup(classGroupId).enqueue(object : Callback<ScheduleResponse> {
            override fun onResponse(call: Call<ScheduleResponse>, response: Response<ScheduleResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val schedules = response.body()?.schedules ?: emptyList()

                    // Cria o adaptador com a lista de horários
                    val adapter = ScheduleAdapter(schedules)
                    recyclerView.adapter = adapter
                } else {
                    Log.e("MainActivity", "Erro HTTP: ${response.code()} - ${response.errorBody()?.string()}")
                    Toast.makeText(this@MainActivity, "Erro ao buscar horários: ${response.body()?.message ?: "Erro desconhecido"}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
                Log.e("MainActivity", "Erro ao conectar ao servidor: ${t.message}", t)
                Toast.makeText(this@MainActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}