package com.example.isctecalendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.isctecalendar.network.ApiService
import com.example.isctecalendar.data.LoginRequest
import com.example.isctecalendar.data.LoginResponse
import com.example.isctecalendar.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }

        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun login(email: String, password: String) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val loginRequest = LoginRequest(email, password)

        apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    // Verifica se o sucesso foi confirmado no body da resposta
                    val body = response.body()
                    if (body?.success == true) {
                        val classGroupId = response.body()?.student?.classGroupId
                        val studentId = response.body()?.student?.number
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("classGroupId", classGroupId)
                        intent.putExtra("studentId", studentId)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, body?.message ?: "Erro no login", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Lidando com códigos de erro HTTP fora do intervalo 200-299
                    when (response.code()) {
                        401 -> {
                            Toast.makeText(this@LoginActivity, "Credenciais inválidas.", Toast.LENGTH_SHORT).show()
                        }
                        500 -> {
                            Toast.makeText(this@LoginActivity, "Erro no servidor. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this@LoginActivity, "Erro inesperado: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    // Log adicional para análise
                    Log.e("LoginActivity", "Erro HTTP: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("LoginActivity", "Erro ao conectar ao servidor", t)
            }
        })
    }
}
