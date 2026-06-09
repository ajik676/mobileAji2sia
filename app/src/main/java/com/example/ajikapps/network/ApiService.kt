package com.example.ajikapps.network

import com.example.ajikapps.home.PostResponse
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<PostResponse>
}
