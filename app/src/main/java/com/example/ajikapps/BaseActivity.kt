package com.example.ajikapps

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.ajikapps.about.AboutFragment
import com.example.ajikapps.databinding.ActivityBaseBinding
import com.example.ajikapps.home.HomeFragment
import com.example.ajikapps.list.ListFragment
import com.example.ajikapps.profile.ProfileFragment

class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding

    // Simpan username di level class agar mudah diakses
    private var username: String = "Pengguna"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Inisialisasi ViewBinding
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Penyesuaian Edge-to-Edge agar layout tidak tertutup status bar / navigasi bawah
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ambil data username dari intent
        username = intent.getStringExtra("extra_username") ?: "Pengguna"

        // Setup fragment default HANYA saat aplikasi pertama kali dibuat
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment.newInstance(username))
            // Opsional: Pastikan tab home aktif secara visual
            // binding.bottomNavView.selectedItemId = R.id.home
        }

        // Handle aksi klik pada Bottom Navigation
        binding.bottomNavView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment.newInstance(username))
                    true
                }
                R.id.list -> {
                    replaceFragment(ListFragment())
                    true
                }
                R.id.about -> {
                    replaceFragment(AboutFragment())
                    true
                }
                R.id.profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Fungsi helper untuk mengganti fragment
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment) // Pastikan ID di XML adalah fragmentContainer
            .commit()
    }
}