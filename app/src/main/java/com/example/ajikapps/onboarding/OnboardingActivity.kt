package com.example.ajikapps.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.ajikapps.R
import com.example.ajikapps.AuthActivity
import com.example.ajikapps.databinding.ActivityOnboardingBinding
import com.example.ajikapps.utils.PreferenceManager

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter
    private val slides = listOf(
        OnboardingSlide(
            "Selamat Datang",
            "Aplikasi Bina Desa membantu masyarakat mengakses layanan surat secara digital.",
            R.drawable.ic_welcome
        ),
        OnboardingSlide(
            "Layanan Surat Cepat",
            "Ajukan berbagai kebutuhan surat desa dengan mudah dan praktis.",
            R.drawable.ic_mail
        ),
        OnboardingSlide(
            "Informasi Berita",
            "Dapatkan berita terbaru dan informasi penting langsung dari aplikasi.",
            R.drawable.ic_news
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupIndicators()
        setCurrentIndicator(0)

        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < slides.size - 1) {
                binding.viewPager.currentItem = currentItem + 1
            }
        }

        binding.btnSkip.setOnClickListener {
            navigateToMain()
        }

        binding.btnGetStarted.setOnClickListener {
            navigateToMain()
        }
    }

    private fun setupViewPager() {
        onboardingAdapter = OnboardingAdapter(slides)
        binding.viewPager.adapter = onboardingAdapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                
                if (position == slides.size - 1) {
                    binding.btnNext.visibility = ViewGroup.GONE
                    binding.btnSkip.visibility = ViewGroup.GONE
                    binding.btnGetStarted.visibility = ViewGroup.VISIBLE
                } else {
                    binding.btnNext.visibility = ViewGroup.VISIBLE
                    binding.btnSkip.visibility = ViewGroup.VISIBLE
                    binding.btnGetStarted.visibility = ViewGroup.GONE
                }
            }
        })
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(slides.size)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 0, 8, 0)
        }

        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.dot_inactive
                )
            )
            indicators[i]?.layoutParams = layoutParams
            binding.layoutDots.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.layoutDots.childCount
        for (i in 0 until childCount) {
            val imageView = binding.layoutDots.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.dot_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.dot_inactive
                    )
                )
            }
        }
    }

    private fun navigateToMain() {
        // Simpan status onboarding completed menggunakan PreferenceManager
        PreferenceManager(this).setOnboardingCompleted(true)
        
        // Pindah ke AuthActivity
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}
