package com.example.ajikapps

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.ajikapps.databinding.BinadesaWebviewBinding

class BinaDesaWebView : AppCompatActivity() {

    private lateinit var binding: BinadesaWebviewBinding

    // SharedPreferences
    private val PREF_NAME = "MyPrefs"
    private val KEY_URL = "last_url"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = BinadesaWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Ambil URL tersimpan, kalau kosong pakai default
        val savedUrl = sharedPref.getString(
            KEY_URL,
            "https://hirzi24si.alwaysdata.net"
        )

        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true

        binding.webView.loadUrl(savedUrl!!)

        // Tombol back
        binding.btnBack.setOnClickListener {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                finish()
            }
        }
    }

    // Simpan URL saat activity ditutup
    override fun onPause() {
        super.onPause()

        val editor = getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        ).edit()

        editor.putString(
            KEY_URL,
            binding.webView.url
        )

        editor.apply()
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            finish()
        }
    }
}