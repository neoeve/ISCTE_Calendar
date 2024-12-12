package com.example.isctecalendar.network
import com.example.isctecalendar.data.AttendanceRequest
import com.example.isctecalendar.data.ClassRoomResponse
import com.example.isctecalendar.data.LoginRequest
import com.example.isctecalendar.data.LoginResponse
import com.example.isctecalendar.data.PresenceResponse
import com.example.isctecalendar.data.RegisterRequest
import com.example.isctecalendar.data.RegisterResponse
import com.example.isctecalendar.data.ScheduleResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


// Interface com os endpoints
interface ApiService {
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @GET("schedule/classGroup/{classGroupId}")
    fun getScheduleByClassGroup(@Path("classGroupId") classGroupId: Int): Call<ScheduleResponse>

    @POST("attendance")
    fun markAttendance(@Body attendanceRequest: AttendanceRequest): Call<Void>

    @POST("/attendance/mark-presence")
    fun markPresence(@Body requestBody: Map<String, Int>): Call<PresenceResponse>

    @GET("/qrcode/classroom/{id}")
    fun getClassRoomDetails(@Path("id") classRoomId: Int): Call<ClassRoomResponse>
}
