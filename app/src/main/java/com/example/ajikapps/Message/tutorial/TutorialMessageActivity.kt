package com.example.ajikapps.Message.tutorial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ajikapps.databinding.ActivityTutorialMessageBinding

class TutorialMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTutorialMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup ViewPager dengan adapter
        val fragmentsList = listOf(
            Tutorial1Fragment(),
            Tutorial2Fragment(),
            Tutorial3Fragment()
        )
        val adapter = TutorialFragmentAdapter(this, fragmentsList)
        binding.tutorialMessageViewPager.adapter = adapter

        // Hubungkan DotsIndicator dengan ViewPager2
        binding.dotIndicator.attachTo(binding.tutorialMessageViewPager)
    }
}