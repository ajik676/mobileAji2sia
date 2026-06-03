package com.example.ajikapps.Message

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ajikapps.R
import com.example.ajikapps.Message.tutorial.TutorialAdapter
import com.example.ajikapps.Message.tutorial.TutorialMessageActivity
import com.example.ajikapps.databinding.FragmentMessageBinding
import com.google.android.material.tabs.TabLayoutMediator

class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Message"
        setHasOptionsMenu(true)

        // Setup ViewPager2 dengan Adapter
        val adapter = TutorialAdapter(this)
        binding.viewPagerTutorial.adapter = adapter

        // Hubungkan TabLayout (sebagai indikator) dengan ViewPager2
        TabLayoutMediator(binding.tabLayoutIndicator, binding.viewPagerTutorial) { _, _ ->
            // Biarkan kosong karena kita hanya butuh dot indikator
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.message_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_tutorial -> {
                val intent = Intent(requireContext(), TutorialMessageActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}