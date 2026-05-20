package com.example.ajikapps.home.pertemuan_9

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.chip.Chip
import com.example.ajikapps.databinding.ActivityNinthBinding

class NinthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNinthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNinthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Menghilangkan error saat user mulai mengetik
        binding.etEmail.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                binding.tilEmail.error = null
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            if (email.isEmpty()) {
                binding.tilEmail.error = "Email tidak boleh kosong"
            } else {
                binding.tilEmail.error = null
                Toast.makeText(this, "Login Berhasil: $email", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener untuk ChipGroup
        binding.chipGroupFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedChipId = checkedIds.firstOrNull() // Ambil ID chip yang dipilih
            if (selectedChipId != null) {
                val chip = group.findViewById<Chip>(selectedChipId)
                Toast.makeText(this, "Filter: ${chip.text}", Toast.LENGTH_SHORT).show()
                // Lakukan logika filter di sini
            }
        }
    }
}