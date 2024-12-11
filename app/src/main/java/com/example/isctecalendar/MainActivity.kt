package com.example.isctecalendar

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.isctecalendar.network.ApiService
import com.example.isctecalendar.network.RetrofitClient
import com.example.isctecalendar.data.ScheduleResponse
import com.example.isctecalendar.data.ScheduleItem
import com.example.isctecalendar.adapters.ScheduleAdapter
import com.example.isctecalendar.data.AttendanceRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var scheduleAdapter: ScheduleAdapter
    private val scheduleItems = mutableListOf<ScheduleItem>() // Lista de ScheduleItem
    private var userId: Int = -1 // ID do usuário

    private fun formatMonth(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        val outputFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault()) // Mês por extenso
        return outputFormat.format(date!!)
    }

    private fun formatDay(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        val outputFormat = SimpleDateFormat("EEEE, dd", Locale.getDefault()) // Dia da semana e dia
        return outputFormat.format(date!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Obtém o ID do aluno a partir da Intent
        userId = intent.getIntExtra("studentId", -1)

        if (userId == -1) {
            Toast.makeText(this, "Erro: ID do utilizador não encontrado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configura RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        scheduleAdapter = ScheduleAdapter(scheduleItems) { scheduleId, isAttending ->
            markAttendance(scheduleId, userId, isAttending)
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
        apiService.getScheduleByClassGroup(classGroupId).enqueue(object : Callback<ScheduleResponse> {
            override fun onResponse(call: Call<ScheduleResponse>, response: Response<ScheduleResponse>) {
                if (response.isSuccessful) {
                    response.body()?.schedules?.let { schedules ->
                        scheduleItems.clear()

                        // Agrupar por mês e por dia
                        val groupedByMonth = schedules.groupBy { it.date.substring(0, 7) } // "yyyy-MM"
                        groupedByMonth.forEach { (month, schedulesForMonth) ->
                            scheduleItems.add(ScheduleItem.Header(formatMonth(month))) // Cabeçalho do mês

                            // Agrupar por dia
                            val groupedByDay = schedulesForMonth.groupBy { it.date }
                            groupedByDay.forEach { (day, schedulesForDay) ->
                                scheduleItems.add(ScheduleItem.Header(formatDay(day))) // Cabeçalho do dia

                                // Adicionar os detalhes do horário
                                schedulesForDay.forEach { schedule ->
                                    scheduleItems.add(ScheduleItem.ScheduleDetail(
                                        id = schedule.id,
                                        startTime = schedule.startTime,
                                        endTime = schedule.endTime,
                                        subject = schedule.subject,
                                        classRoom = schedule.classRoom
                                    ))
                                }
                            }
                        }

                        scheduleAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Erro ao buscar horários.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun markAttendance(scheduleId: Int, studentId: Int, isAttending: Boolean) {
        val attendanceRequest = AttendanceRequest(
            scheduleId = scheduleId,
            userId = studentId,
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
