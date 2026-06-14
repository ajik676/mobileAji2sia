package com.example.ajikapps.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surat_services")
data class SuratServiceEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val iconName: String,
    val requirements: String
)
