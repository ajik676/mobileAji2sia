package com.example.ajikapps.data.remote

import com.example.ajikapps.home.PostResponse
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<PostResponse>
}
