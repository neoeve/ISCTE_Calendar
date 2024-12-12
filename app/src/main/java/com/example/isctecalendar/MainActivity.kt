package com.example.isctecalendar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var scheduleAdapter: ScheduleAdapter
    private val scheduleItems = mutableListOf<ScheduleItem>()
    private var userId: Int = -1 // ID do utilizador
    private lateinit var qrCodeLauncher: ActivityResultLauncher<Intent>

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


        // Inicializa o ActivityResultLauncher
        qrCodeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val scanResult = IntentIntegrator.parseActivityResult(IntentIntegrator.REQUEST_CODE, result.resultCode, intent)
                if (scanResult != null && scanResult.contents != null) {
                    handleQrCodeResult(scanResult.contents) // Chama o método para processar o QR Code
                } else {
                    Toast.makeText(this, "Leitura cancelada", Toast.LENGTH_SHORT).show()
                }
            }
        }

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
        val scanButton = findViewById<ImageButton>(R.id.scanButton)
        scanButton.setOnClickListener { startQrCodeScanner() }
        scanButton.visibility = View.VISIBLE
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
        val integrator = IntentIntegrator(this)
        integrator.setCaptureActivity(CustomCaptureActivity::class.java)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Leia o QRCode da Sala")
        integrator.setCameraId(0) // Use a câmera traseira
        integrator.setBeepEnabled(false) // Som ao ler QR
        integrator.setBarcodeImageEnabled(false)
        integrator.setOrientationLocked(true)
        qrCodeLauncher.launch(integrator.createScanIntent()) // Usa o ActivityResultLauncher
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Leitura cancelada", Toast.LENGTH_SHORT).show()
            } else {
                handleQrCodeResult(result.contents) // Chama o método para processar o QR Code
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun validateQrCode(roomId: Int) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        apiService.validateQrCode(roomId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Presença confirmada na sala!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Erro ao validar presença.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleQrCodeResult(contents: String) {
        try {
            // Decodifica o JSON do QR Code
            val qrData = JSONObject(contents)
            val roomId = qrData.getInt("roomId") // Obtém o ID da sala do QR Code
            val roomName = qrData.getString("name") // Nome da sala, opcional

            // Mostra mensagem temporária
            Toast.makeText(this, "QR Code da sala: $roomName (ID: $roomId)", Toast.LENGTH_SHORT).show()

            // Valida o QR Code com o backend
            validateQrCode(roomId)
        } catch (e: JSONException) {
            Toast.makeText(this, "QR Code inválido", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
