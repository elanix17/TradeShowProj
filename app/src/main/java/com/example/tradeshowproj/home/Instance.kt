package com.example.tradeshowproj.home

import com.example.tradeshowproj.zCatalystSDK.ZApiSDK
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://tradeshow-60036155049.development.catalystserverless.in/"
//    private const val BASE_URL = "https://www.google.com"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(
            token = "50024534997.04459d456574ca941b8eff5bbd44d5fe.654be865caae26c68a89e60b4196e378"
        ))
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // Use our custom OkHttpClient
            .build()
            .create(ApiService::class.java)
    }
}