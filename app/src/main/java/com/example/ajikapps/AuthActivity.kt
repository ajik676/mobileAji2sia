package com.example.ajikapps

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ajikapps.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)

        // kalau sudah login → langsung ke BaseActivity
        if (sharedPref.getBoolean("isLogin", false)) {
            val intent = Intent(this, BaseActivity::class.java)
            intent.putExtra("extra_username", sharedPref.getString("username", "Pengguna"))
            startActivity(intent)
            finish()
            return
        }

        binding.btnLogin.setOnClickListener {

            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty()) {
                binding.etUsername.error = "Username wajib diisi"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.etPassword.error = "Password wajib diisi"
                return@setOnClickListener
            }

            // login sederhana
            if (username == password) {

                sharedPref.edit().apply {
                    putBoolean("isLogin", true)
                    putString("username", username)
                    apply()
                }

                Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, BaseActivity::class.java)
                intent.putExtra("extra_username", username)
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(this, "Username atau Password salah", Toast.LENGTH_SHORT).show()
            }
        }
    }
}