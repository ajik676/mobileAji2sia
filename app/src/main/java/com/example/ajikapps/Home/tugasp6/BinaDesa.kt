package com.example.ajikapps

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ajikapps.databinding.ActivityBinadesaBinding
import com.example.ajikapps.BinaDesaWebView

class BinaDesa : AppCompatActivity() {

    private lateinit var binding: ActivityBinadesaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBinadesaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref =
            getSharedPreferences(
                "user_pref",
                MODE_PRIVATE
            )

        // Tombol masuk website (WebView)
        binding.btnWebsite.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    BinaDesaWebView::class.java
                )
            )
        }

        // Tombol logout
        binding.btnLogout.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Yakin ingin logout?")

                .setPositiveButton("Ya") { dialog, _ ->

                    val editor = sharedPref.edit()
                    editor.clear()
                    editor.apply()

                    dialog.dismiss()

                    val intent = Intent(
                        this,
                        SplashScreenActivity::class.java
                    )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                    finish()
                }

                .setNegativeButton("Tidak", null)
                .show()
        }
    }
}