package com.example.ajikapps

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_splash_screen
        )

        lifecycleScope.launch {

            delay(2000)

            val sharedPref =
                getSharedPreferences(
                    "user_pref",
                    MODE_PRIVATE
                )

            val isLogin =
                sharedPref.getBoolean(
                    "isLogin",
                    false
                )

            // JIKA SUDAH LOGIN -> LANGSUNG KE BINA DESA
            val intent =
                if (isLogin) {

                    Intent(
                        this@SplashScreenActivity,
                        BinaDesa::class.java
                    )

                } else {

                    Intent(
                        this@SplashScreenActivity,
                        AuthActivity::class.java
                    )
                }

            startActivity(intent)
            finish()
        }
    }
}