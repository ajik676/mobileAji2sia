package com.example.ajikapps

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ajikapps.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPref = getSharedPreferences("user_pref", Context.MODE_PRIVATE)

        // Cek login
        if (sharedPref.getBoolean("isLogin", false)) {
            val intent = Intent(this, BaseActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi semua bidang!", Toast.LENGTH_SHORT).show()
            } else if (username == password) {
                sharedPref.edit().apply {
                    putBoolean("isLogin", true)
                    putString("username", username)
                    apply()
                }

                startActivity(Intent(this, BaseActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Gagal! Username & Password harus sama", Toast.LENGTH_SHORT).show()
            }
        }
    }
}