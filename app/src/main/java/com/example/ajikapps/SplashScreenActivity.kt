package com.example.ajikapps

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ajikapps.AuthActivity
import com.example.ajikapps.onboarding.OnboardingActivity
import com.example.ajikapps.utils.PreferenceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        lifecycleScope.launch {
            // Delay for 2 seconds to display splash screen
            delay(2000)

            val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
            val isLogin = sharedPref.getBoolean("isLogin", false)

            val intent = if (isLogin) {
                Intent(this@SplashScreenActivity, BaseActivity::class.java).apply {
                    putExtra("extra_username", sharedPref.getString("username", "Pengguna"))
                }
            } else {
                val prefManager = PreferenceManager(this@SplashScreenActivity)
                if (prefManager.isOnboardingCompleted()) {
                    Intent(this@SplashScreenActivity, AuthActivity::class.java)
                } else {
                    Intent(this@SplashScreenActivity, OnboardingActivity::class.java)
                }
            }

            startActivity(intent)
            finish()
        }
    }
}