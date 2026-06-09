package com.example.ajikapps.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ajikapps.SplashScreenActivity
import com.example.ajikapps.databinding.FragmentHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var beritaAdapter: BeritaAdapter

    companion object {
        fun newInstance(username: String): HomeFragment {
            val fragment = HomeFragment()
            val bundle = Bundle()
            bundle.putString("username", username)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences(
            "user_pref",
            Context.MODE_PRIVATE
        )

        // 1. Mengambil username dari bundle, default "Warga Desa" jika kosong
        val username = arguments?.getString("username") ?: "Warga Desa"

        // 2. Mengubah teks pada TextView tvUsername agar menyapa user di Header
        binding.tvUsername.text = username

        // 3. Setup RecyclerView untuk Berita
        setupNewsRecyclerView()

        // 4. Setup ViewModel untuk Berita
        setupNewsViewModel()

        // 5. Tombol Refresh/Coba Lagi Berita
        binding.btnRefreshNews.setOnClickListener {
            newsViewModel.fetchNews()
        }

        // 6. Tombol Logout (Tetap menggunakan btnLogout dari XML baru)
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Keluar")
                .setMessage("Yakin ingin keluar dari akun Anda?")
                .setPositiveButton("Ya") { dialog, _ ->

                    // Hapus sesi user
                    sharedPref.edit().clear().apply()
                    dialog.dismiss()

                    // Pindah ke halaman Splash/Login
                    val intent = Intent(
                        requireContext(),
                        SplashScreenActivity::class.java
                    )
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }
    }

    private fun setupNewsRecyclerView() {
        beritaAdapter = BeritaAdapter(emptyList()) { news ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(news.title)
                .setMessage("${news.date}\n\n${news.description}")
                .setPositiveButton("Tutup") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.rvBeritaNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = beritaAdapter
        }
    }

    private fun setupNewsViewModel() {
        newsViewModel = ViewModelProvider(this)[NewsViewModel::class.java]

        newsViewModel.newsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NewsState.Loading -> {
                    binding.progressBarNews.visibility = View.VISIBLE
                    binding.rvBeritaNews.visibility = View.GONE
                    binding.layoutEmptyStateNews.visibility = View.GONE
                }
                is NewsState.Success -> {
                    binding.progressBarNews.visibility = View.GONE
                    binding.layoutEmptyStateNews.visibility = View.GONE
                    binding.rvBeritaNews.visibility = View.VISIBLE
                    beritaAdapter.updateData(state.news)
                }
                is NewsState.Error -> {
                    binding.progressBarNews.visibility = View.GONE
                    binding.rvBeritaNews.visibility = View.GONE
                    binding.layoutEmptyStateNews.visibility = View.VISIBLE
                    binding.tvEmptyStateMessageNews.text = state.message
                }
            }
        }

        // Ambil berita saat awal dimuat
        newsViewModel.fetchNews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}