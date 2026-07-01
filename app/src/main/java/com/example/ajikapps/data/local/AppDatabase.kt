package com.example.ajikapps.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [SuratServiceEntity::class, SuratRequestEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun suratDao(): SuratDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bina_desa_db"
                )
                .fallbackToDestructiveMigration() // Handle database structure changes
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                populateDatabase(database.suratDao())
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateDatabase(suratDao: SuratDao) {
            val services = listOf(
                SuratServiceEntity(
                    1,
                    "Surat Keterangan Domisili",
                    "Digunakan untuk keterangan domisili tinggal warga.",
                    "ic_domisili",
                    "• KTP asli\n• KK asli\n• Surat Pengantar RT/RW"
                ),
                SuratServiceEntity(
                    2,
                    "Surat Keterangan Usaha",
                    "Digunakan untuk pengantar izin usaha atau KUR.",
                    "ic_usaha",
                    "• KTP asli\n• KK asli\n• Pengantar RT/RW\n• Foto Lokasi Usaha"
                ),
                SuratServiceEntity(
                    3,
                    "Surat Pengantar SKCK",
                    "Digunakan untuk syarat pengajuan SKCK ke Kepolisian.",
                    "ic_skck",
                    "• KTP asli\n• KK asli\n• Pengantar RT/RW\n• Foto 4x6 (Latar Merah)"
                ),
                SuratServiceEntity(
                    4,
                    "Surat Keterangan Tidak Mampu",
                    "Digunakan untuk pengajuan beasiswa atau bantuan.",
                    "ic_tidak_mampu",
                    "• KTP asli\n• KK asli\n• Pengantar RT/RW\n• Surat Pernyataan Miskin"
                ),
                SuratServiceEntity(
                    5,
                    "Surat Kelahiran",
                    "Digunakan untuk pencatatan kelahiran anak baru.",
                    "ic_kelahiran",
                    "• KK asli\n• KTP Orang Tua\n• Surat Nikah Orang Tua\n• Surat Keterangan Bidan/RS"
                ),
                SuratServiceEntity(
                    6,
                    "Surat Kematian",
                    "Digunakan untuk pencatatan kematian kerabat.",
                    "ic_kematian",
                    "• KTP Jenazah\n• KK Jenazah/Asli\n• Surat Keterangan RS/Dokter\n• KTP Pelapor"
                ),
                SuratServiceEntity(
                    7,
                    "Surat Pindah Penduduk",
                    "Digunakan untuk proses perpindahan alamat keluar.",
                    "ic_pindah",
                    "• KTP asli\n• KK asli\n• Surat Pengantar RT/RW\n• Alamat Lengkap Tujuan Baru"
                ),
                SuratServiceEntity(
                    8,
                    "Surat Pengantar Nikah",
                    "Digunakan untuk pengantar nikah ke KUA.",
                    "ic_nikah",
                    "• KTP Calon Pengantin\n• KK asli\n• KTP Orang Tua\n• Pengantar RT/RW\n• Pas Foto Calon Pengantin"
                )
            )
            suratDao.insertServices(services)
        }
    }
}
