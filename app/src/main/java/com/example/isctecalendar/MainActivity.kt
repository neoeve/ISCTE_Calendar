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
    private val scheduleList = mutableListOf<Schedule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configura RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        scheduleAdapter = ScheduleAdapter(scheduleList) { scheduleId, isAttending ->
            markAttendance(scheduleId, isAttending)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = scheduleAdapter

        // Busca os horários
        fetchSchedules()
    }

    private fun fetchSchedules() {
        val classGroupId = intent.getIntExtra("classGroupId", -1)
        if (classGroupId == -1) {
            Toast.makeText(this, "Erro: ID do grupo de classe não encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getScheduleByClassGroup(classGroupId).enqueue(object : Callback<List<Schedule>> {
            override fun onResponse(call: Call<List<Schedule>>, response: Response<List<Schedule>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        scheduleList.clear()
                        scheduleList.addAll(it)
                        scheduleAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Erro ao buscar horários.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Schedule>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun markAttendance(scheduleId: Int, isAttending: Boolean) {
        val userId = 123 // Substitua pelo ID real do utilizador

        val attendanceRequest = AttendanceRequest(
            scheduleId = scheduleId,
            userId = userId,
            isAttending = isAttending
        )

        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.markAttendance(attendanceRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Presença atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Erro ao atualizar presença.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}