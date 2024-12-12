package com.example.isctecalendar

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.isctecalendar.network.ApiService
import com.example.isctecalendar.network.RetrofitClient
import com.example.isctecalendar.data.ScheduleItem
import com.example.isctecalendar.adapters.ScheduleAdapter
import com.example.isctecalendar.data.AttendanceRequest
import com.example.isctecalendar.data.ScheduleResponse
import com.google.zxing.integration.android.IntentIntegrator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Button
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var scheduleAdapter: ScheduleAdapter
    private val scheduleItems = mutableListOf<ScheduleItem>()
    private var userId: Int = -1 // ID do utilizador

    private fun formatarMes(data: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val mesFormatado = SimpleDateFormat("MMMM yyyy", Locale("pt", "PT"))
        return sdf.parse(data)?.let { mesFormatado.format(it).replaceFirstChar { it.uppercase() } } ?: data
    }

    private fun formatarDia(data: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val diaFormatado = SimpleDateFormat("EEEE, d", Locale("pt", "PT"))
        return sdf.parse(data)?.let { diaFormatado.format(it).replaceFirstChar { it.uppercase() } } ?: data
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

        // Configurar leitura de QR Code
        val scanButton = findViewById<Button>(R.id.scanButton)
        scanButton.setOnClickListener { startQrCodeScanner() }
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
                    response.body()?.let { scheduleResponse ->
                        if (scheduleResponse.success) {
                            val schedules = scheduleResponse.schedules
                            scheduleItems.clear()

                            // Agrupamento por mês
                            val groupedByMonth = schedules.groupBy { schedule ->
                                formatarMes(schedule.date)
                            }

                            groupedByMonth.forEach { (month, schedulesInMonth) ->
                                // Adiciona o cabeçalho do mês
                                scheduleItems.add(ScheduleItem.Header(month))

                                // Agrupa os horários dentro do mês por dia
                                val groupedByDay = schedulesInMonth.groupBy { schedule ->
                                    formatarDia(schedule.date)
                                }

                                groupedByDay.forEach { (day, schedulesInDay) ->
                                    // Adiciona o cabeçalho do dia
                                    scheduleItems.add(ScheduleItem.Header(day))

                                    // Adiciona os detalhes das aulas
                                    schedulesInDay.forEach { schedule ->
                                        scheduleItems.add(
                                            ScheduleItem.ScheduleDetail(
                                                id = schedule.id,
                                                startTime = schedule.startTime,
                                                endTime = schedule.endTime,
                                                subject = schedule.subject,
                                                classRoom = schedule.classRoom
                                            )
                                        )
                                    }
                                }
                            }

                            scheduleAdapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(this@MainActivity, scheduleResponse.message ?: "Erro ao buscar horários.", Toast.LENGTH_SHORT).show()
                        }
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

    private fun startQrCodeScanner() {
        IntentIntegrator(this).apply {
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            setPrompt("Posicione o QR Code dentro da área")
            setCameraId(0) // Use a câmera traseira
            setBeepEnabled(true) // Som ao ler QR
            setBarcodeImageEnabled(false)
            initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Leitura cancelada", Toast.LENGTH_SHORT).show()
            } else {
                handleQrCodeResult(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleQrCodeResult(contents: String) {
        // Processar o conteúdo do QR Code
        Toast.makeText(this, "QR Code lido: $contents", Toast.LENGTH_SHORT).show()

        // Exemplo: usar o ID da aula para marcar presença
        val scheduleId = contents.toIntOrNull()
        if (scheduleId != null) {
            markAttendance(scheduleId, userId, true)
        } else {
            Toast.makeText(this, "QR Code inválido", Toast.LENGTH_SHORT).show()
        }
    }
}
