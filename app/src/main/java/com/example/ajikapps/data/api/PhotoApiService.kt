package com.example.ajikapps.data.api

import com.example.ajikapps.data.model.PhotoModel
import retrofit2.http.GET

interface PhotoApiService {
    @GET("list")
    suspend fun getPhotos(): List<PhotoModel>
}