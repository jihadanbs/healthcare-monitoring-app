package com.example.healthcaremonitoringapp.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5000/api/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .header("Content-Type", "application/json")
                .method(originalRequest.method, originalRequest.body)
            chain.proceed(requestBuilder.build())
        }
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val instance: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val apiService: PatientApiService by lazy {
        retrofit.create(PatientApiService::class.java)
    }
}