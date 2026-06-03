package com.example.ajikapps.Message.tutorial

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ajikapps.Message.MessageFragment

class TutorialAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Tutorial1Fragment()
            1 -> Tutorial2Fragment()
            2 -> Tutorial3Fragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}