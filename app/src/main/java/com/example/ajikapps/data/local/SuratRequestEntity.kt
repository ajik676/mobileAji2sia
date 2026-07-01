package com.example.ajikapps.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surat_requests")
data class SuratRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serviceId: Int,
    val serviceTitle: String,
    val username: String,
    val nik: String,
    val fullName: String,
    val phone: String,
    val purpose: String,
    val status: String,
    val date: String,
    val documentPhotoPath: String? = null // New field for Camera Capture
)
