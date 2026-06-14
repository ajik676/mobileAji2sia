package com.example.ajikapps.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SuratDao {

    // Services Queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<SuratServiceEntity>)

    @Query("SELECT * FROM surat_services ORDER BY id ASC")
    suspend fun getAllServices(): List<SuratServiceEntity>

    @Query("SELECT * FROM surat_services WHERE id = :id")
    suspend fun getServiceById(id: Int): SuratServiceEntity?

    // Requests Queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: SuratRequestEntity)

    @Query("SELECT * FROM surat_requests ORDER BY id DESC")
    fun getAllRequests(): LiveData<List<SuratRequestEntity>>

    @Query("SELECT * FROM surat_requests WHERE username = :username ORDER BY id DESC")
    fun getRequestsByUsername(username: String): LiveData<List<SuratRequestEntity>>

    @Delete
    suspend fun deleteRequest(request: SuratRequestEntity)
}
