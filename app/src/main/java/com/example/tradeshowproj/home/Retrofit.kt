package com.example.tradeshowproj.home

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("server/card/createDeckAndCards")
    suspend fun createPost(@Body request: PostRequest): Response<Void>
}