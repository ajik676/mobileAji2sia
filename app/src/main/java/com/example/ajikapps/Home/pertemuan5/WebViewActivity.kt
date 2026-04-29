package com.example.ajikapps.pertemuan_5

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.ajikapps.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Web Jual Beli Motor"
            setDisplayHomeAsUpEnabled(true)
        }

        // Setup WebView
        binding.webview.webViewClient = WebViewClient()
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.loadUrl("https://www.olx.co.id/motor-bekas_c200?utm_source=google&utm_medium=cpc&utm_campaign=ID|MIX|CLA|PM|GP|REM|GEN|CPC|PageView|BrandingCampaign_2Wheeler|MIX|PLP|20241210&utm_term=&utm_content=&campaign_id=22004639475&group_id=&target_id=&ad_id=&matchtype=&network=x&device=c&country=ID&territory=MIX&group=&campaign_type=PM&gad_source=1&gad_campaignid=22014873004&gbraid=0AAAAADkt5UccOzg7Y6VlR99CIzT4nz692&gclid=CjwKCAjw7vzOBhBxEiwAc7WNrxaII4S922me70kFaL39O9plxougkR2Eci2c5d1TlA1PmTno4r8RVRoCArIQAvD_BwE")

        // Hide / Show AppBar saat scroll
        binding.webview.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                binding.appbar.setExpanded(false, true) // hide
            } else {
                binding.appbar.setExpanded(true, true) // show
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (binding.webview.canGoBack()) {
            binding.webview.goBack()
        } else {
            super.onBackPressed()
        }
    }
}