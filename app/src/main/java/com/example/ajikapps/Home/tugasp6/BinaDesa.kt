package com.example.ajikapps

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ajikapps.databinding.ActivityBinadesaBinding

class BinaDesa : AppCompatActivity() {

    private lateinit var binding: ActivityBinadesaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)

        // 🔒 proteksi: kalau belum login → balik ke auth
        if (!sharedPref.getBoolean("isLogin", false)) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        binding = ActivityBinadesaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // tombol website
        binding.btnWebsite.setOnClickListener {
            startActivity(Intent(this, BinaDesaWebView::class.java))
        }

        // logout
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Yakin ingin logout?")
                .setPositiveButton("Ya") { dialog, _ ->

                    sharedPref.edit().clear().apply()
                    dialog.dismiss()

                    val intent = Intent(this, AuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }
    }
}