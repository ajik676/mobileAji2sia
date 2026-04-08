package com.example.ajikapps.pertemuan3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ajikapps.R
import com.example.ajikapps.databinding.ActivityThirdBinding
import com.example.ajikapps.pertemuan3.ThirdResaultActivity

class ThirdActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThirdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // val no: EditText = findViewById(R.id.inputNoTujuan)
        //val kirim: Button = findViewById(R.id.btnKirim)

        binding.btnKirim.setOnClickListener {
            val no = binding.inputNoTujuan.text
            Toast.makeText(this, "pesan berhasi di kirim ke $no", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ThirdResaultActivity::class.java)
            startActivity(intent)
        }
    }

}
