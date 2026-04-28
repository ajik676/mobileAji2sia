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

        val sharedPref =
            getSharedPreferences(
                "user_pref",
                MODE_PRIVATE
            )

        binding.btnLogin.setOnClickListener {

            val username =
                binding.edtUsername.text.toString().trim()

            val password =
                binding.edtPassword.text.toString().trim()

            if (username.isEmpty()) {
                binding.edtUsername.error = "Username wajib diisi"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.edtPassword.error = "Password wajib diisi"
                return@setOnClickListener
            }

            // Jika username = password login sukses
            if (username == password) {

                val editor = sharedPref.edit()

                editor.putBoolean(
                    "isLogin",
                    true
                )

                editor.putString(
                    "username",
                    username
                )

                editor.apply()

                Toast.makeText(
                    this,
                    "Login Berhasil",
                    Toast.LENGTH_SHORT
                ).show()

                // LANGSUNG MASUK KE BINA DESA
                val intent = Intent(
                    this,
                    BinaDesa::class.java
                )

                startActivity(intent)
                finish()

            } else {

                Toast.makeText(
                    this,
                    "Username atau Password salah",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}