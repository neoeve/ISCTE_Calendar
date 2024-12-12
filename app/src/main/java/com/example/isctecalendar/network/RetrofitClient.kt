package com.example.isctecalendar.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private fun getBaseUrl(): String {
        val buildFingerprint = android.os.Build.FINGERPRINT
        val buildModel = android.os.Build.MODEL
        val buildBrand = android.os.Build.BRAND
        val buildDevice = android.os.Build.DEVICE
        val buildManufacturer = android.os.Build.MANUFACTURER

        val isEmulator = (buildFingerprint.contains("generic")
                || buildModel.contains("google_sdk")
                || buildModel.contains("Emulator")
                || buildBrand.startsWith("generic")
                || buildDevice.startsWith("generic"))
                || buildManufacturer.contains("Genymotion")

        return if (isEmulator) {
            "http://10.0.2.2:3000" // IP para emulador
        } else {
            "http://192.168.1.189:3000" // IP para fisico
        }
    }

    private val BASE_URL = getBaseUrl()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
