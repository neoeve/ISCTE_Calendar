package com.example.isctecalendar

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.isctecalendar.network.ApiService
import com.example.isctecalendar.network.RetrofitClient
import com.example.isctecalendar.data.RegisterRequest
import com.example.isctecalendar.data.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val numeroEditText = findViewById<EditText>(R.id.numeroEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val turmaEditText = findViewById<EditText>(R.id.turmaEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            val numero = numeroEditText.text.toString().toIntOrNull()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val turma = turmaEditText.text.toString()

            if (numero !=null && email.isNotEmpty() && password.isNotEmpty() && turma.isNotEmpty()) {
                registerUser(numero, email, password, turma)
            } else {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(numero: Int, email: String, password: String, turma: String) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val registerRequest = RegisterRequest(numero, email, password, turma)

        apiService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@RegisterActivity, "Registro bem-sucedido!", Toast.LENGTH_SHORT).show()
                    finish() // Volta para a tela anterior (Login)
                } else {
                    Toast.makeText(this@RegisterActivity, response.body()?.message ?: "Erro ao registrar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
