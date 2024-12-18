package com.example.healthcaremonitoringapp.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5000/api/"

//    private val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor { chain ->
//            val originalRequest = chain.request()
//            val requestBuilder = originalRequest.newBuilder()
//                .header("Content-Type", "application/json")
//                .method(originalRequest.method, originalRequest.body)
//            chain.proceed(requestBuilder.build())
//        }
//        .build()

    // Tambahkan variabel untuk menyimpan token secara dinamis
    private var authToken: String = ""

    // Fungsi untuk mengatur token
    fun setAuthToken(token: String) {
        authToken = token
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer $authToken")  // Token dinamis
                .method(originalRequest.method, originalRequest.body)
            chain.proceed(requestBuilder.build())
        }
        .build()

    // Konfigurasi Retrofit dengan client yang baru
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val instance: AuthApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(RetrofitClient.BASE_URL)
            .client(RetrofitClient.okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(AuthApiService::class.java)
    }

    val apiService: PatientApiService = retrofit.create(PatientApiService::class.java)
}