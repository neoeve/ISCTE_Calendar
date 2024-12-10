package com.example.isctecalendar.network
import com.example.isctecalendar.data.LoginRequest
import com.example.isctecalendar.data.LoginResponse
import com.example.isctecalendar.data.RegisterRequest
import com.example.isctecalendar.data.RegisterResponse
import com.example.isctecalendar.data.Schedule
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


// Interface com os endpoints
interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("schedule/{turma}")
    fun getSchedule(@Path("turma") turma: String): Call<List<Schedule>>

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>
}
